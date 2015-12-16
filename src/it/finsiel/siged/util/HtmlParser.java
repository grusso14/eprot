/*
 * Created on 27-gen-2005
 *
 * 
 */
package it.finsiel.siged.util;

/**
 * Contains different utility functions for manipulating Html based text. This
 * includes functionality for removing and restoring special entities (such as &, <, >,
 * ...) and functionality for removing html tags from the text.
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public final class HtmlParser {

    static Logger logger = Logger.getLogger(HtmlParser.class.getName());

    private static final Pattern BREAK_TO_NL_PATTERN = Pattern.compile(
            "</?br>", Pattern.CASE_INSENSITIVE);

    private static final Pattern P_TO_DOUBLE_NL_PATTERN = Pattern.compile(
            "</p>", Pattern.CASE_INSENSITIVE);

    private static final Pattern DIV_TO_DOUBLE_NL_PATTERN = Pattern.compile(
            "</div>", Pattern.CASE_INSENSITIVE);

    private static final Pattern H_TO_DOUBLE_NL_PATTERN = Pattern.compile(
            "</h\\d>", Pattern.CASE_INSENSITIVE);

    private static final Pattern WHITE_SPACE_REMOVAL_PATTERN = Pattern.compile(
            "\\s+", Pattern.CASE_INSENSITIVE);

    private static final Pattern TRIM_SPACE_PATTERN = Pattern.compile("\n\\s+",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern HEADER_REMOVAL_PATTERN = Pattern.compile(
            "<html[^<]*<body[^>]*>", Pattern.CASE_INSENSITIVE);

    private static final Pattern STRIP_TAGS_PATTERN = Pattern.compile(
            "<[^>]*>", Pattern.CASE_INSENSITIVE);

    private static final Pattern COMMENTS_REMOVAL_PATTERN = Pattern.compile(
            "<!--.*-->", Pattern.CASE_INSENSITIVE + Pattern.DOTALL
                    + Pattern.MULTILINE);

    private static final String EMAIL_STR = "([a-zA-Z0-9]+([_\\.-][a-zA-Z0-9]+)*@([a-zA-Z0-9]+([\\.-][a-zA-Z0-9]+)*)+\\.[a-zA-Z]{2,4})";

    // do the bug [997599] "\\b([^\\s@]+@[^\\s]+)\\b";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_STR);

    private static final Pattern EMAIL_PATTERN_INC_LINK = Pattern.compile(
            "<a[\\s\\n]*href=(\\\")?(mailto:)" + EMAIL_STR + "[^<]*</a>",
            Pattern.CASE_INSENSITIVE);

    private static final String PROT = "(http|https|ftp)";

    private static final String PUNC = ".,:;?!\\-";

    private static final String ANY = "\\S";

    private static final String URL_STR = "\\b" + "(" + "(\\w*(:\\S*)?@)?"
            + PROT + "://" + "[" + ANY + "]+" + ")" + "(?=\\s|$)";

    /*
     * \\b Start at word boundary ( (\\w*(:\\S*)?@)? [user:[pass]]@ - Construct
     * prot + ":// protocol and :// ["+any+"] match literaly anything... )
     * (?=\\s|$) ...until we find whitespace or end of String
     */
    private static final Pattern URL_PATTERN = Pattern.compile(URL_STR,
            Pattern.CASE_INSENSITIVE);

    private static final String URL_REPAIR_STR = "(.*://.*?)" + "("
            + "(&gt;).*|" + "([" + PUNC + "]*)" + "(<br>)?" + ")$";

    private static final String SRC_STR = "=\"cid:.+@.+\"";

    private static final String SRC_REPAIR_STR = "cid:|@\\S+\"";

    private static final Pattern SRC_PATTERN = Pattern.compile(SRC_STR,
            Pattern.CASE_INSENSITIVE);

    private static final Pattern SRC_REPAIR_PATTERN = Pattern
            .compile(SRC_REPAIR_STR);

    /*
     * (.*://.*?)" "something" with :// (could be .*? but then the Pattern would
     * match whitespace) ( (&gt;).* a html-Encoded > followed by anything | or
     * (["+punc+"]*)" any Punctuation ( <br> )? 0 or 1 trailing <br> )$ end of
     * String
     */
    private static final Pattern URL_REPAIR_PATTERN = Pattern
            .compile(URL_REPAIR_STR);

    private static final Pattern URL_PATTERN_INC_LINK = Pattern.compile(
            "<a( |\\n)*?href=(\\\")?" + URL_STR + "(.|\\n)*?</a>",
            Pattern.CASE_INSENSITIVE);

    // TODO : Add more special entities - e.g. accenture chars
    // such as ?

    /** Special entities recognized by restore special entities */
    // The form of the entities must be a regexp!
    private static final String[] SPECIAL_ENTITIES = { "&quot;", "&amp;",
            "&lt;", "&gt;", "&nbsp;", "&iexcl;", "&cent;", "&pound;",
            "&curren;", "&yen;", "&brvbar;", "&sect;", "&uml;", "&copy;",
            "&ordf;", "&laquo;", "&not;", "&shy;", "&reg;", "&macr;", "&deg;",
            "&plusmn;", "&sup2;", "&sup3;", "&acute;", "&micro;", "&para;",
            "&middot;", "&cedil;", "&sup1;", "&ordm;", "&raquo;", "&frac14;",
            "&frac12;", "&frac34;", "&iquest;", "&Agrave;", "&Aacute;",
            "&Acirc;", "&Atilde;", "&Auml;", "&Aring;", "&AElig;", "&Ccedil;",
            "&Egrave;", "&Eacute;", "&Ecirc;", "&Euml;", "&Igrave;",
            "&Iacute;", "&Icirc;", "&Iuml;", "&ETH;", "&Ntilde;", "&Ograve;",
            "&Oacute;", "&Ocirc;", "&Otilde;", "&Ouml;", "&times;", "&Oslash;",
            "&Ugrave;", "&Uacute;", "&Ucirc;", "&Uuml;", "&Yacute;", "&THORN;",
            "&szlig;", "&agrave;", "&aacute;", "&acirc;", "&atilde;", "&auml;",
            "&aring;", "&aelig;", "&ccedil;", "&egrave;", "&eacute;",
            "&ecirc;", "&euml;", "&igrave;", "&iacute;", "&icirc;", "&iuml;",
            "&eth;", "&ntilde;", "&ograve;", "&oacute;", "&ocirc;", "&otilde;",
            "&ouml;", "&divide;", "&oslash;", "&ugrave;", "&uacute;",
            "&ucirc;", "&uuml;", "&yacute;", "&thorn;", "&yuml;" };

    /** Normal chars corresponding to the defined special entities */
    private static final String[] ENTITY_STRINGS = { "\"", "&", "<", ">",
            "\u00a0", "\u00a1", "\u00a2", "\u00a3", "\u00a4", "\u00a5",
            "\u00a6", "\u00a7", "\u00a8", "\u00a9", "\u00aa", "\u00ab",
            "\u00ac", "\u00ad", "\u00ae", "\u00af", "\u00b0", "\u00b1",
            "\u00b2", "\u00b3", "\u00b4", "\u00b5", "\u00b6", "\u00b7",
            "\u00b8", "\u00b9", "\u00ba", "\u00bb", "\u00bc", "\u00bd",
            "\u00be", "\u00bf", "\u00c0", "\u00c1", "\u00c2", "\u00c3",
            "\u00c4", "\u00c5", "\u00c6", "\u00c7", "\u00c8", "\u00c9",
            "\u00ca", "\u00cb", "\u00cc", "\u00cd", "\u00ce", "\u00cf",
            "\u00d0", "\u00d1", "\u00d2", "\u00d3", "\u00d4", "\u00d5",
            "\u00d6", "\u00d7", "\u00d8", "\u00d9", "\u00da", "\u00db",
            "\u00dc", "\u00dd", "\u00de", "\u00df", "\u00e0", "\u00e1",
            "\u00e2", "\u00e3", "\u00e4", "\u00e5", "\u00e6", "\u00e7",
            "\u00e8", "\u00e9", "\u00ea", "\u00eb", "\u00ec", "\u00ed",
            "\u00ee", "\u00ef", "\u00f0", "\u00f1", "\u00f2", "\u00f3",
            "\u00f4", "\u00f5", "\u00f6", "\u00f7", "\u00f8", "\u00f9",
            "\u00fa", "\u00fb", "\u00fc", "\u00fd", "\u00fe", "\u00ff" };

    // private static final Pattern SPECIAL_PATTERN =
    // Pattern.compile("&#(\\d)+;");

    /**
     * Utility classes should not have a public constructor.
     */
    private HtmlParser() {
    }

    /**
     * Strips html tags and removes extra spaces which occurs due to e.g.
     * indentation of the html and the head section, which does not contain any
     * textual information. <br>
     * The conversion rutine does the following: <br>
     * 1. Removes the header from the html file, i.e. everything from the html
     * tag until and including the starting body tag. <br>
     * 2. Replaces multiple consecutive whitespace characters with a single
     * space (since extra whitespace should be ignored in html). <br>
     * 3. Replaces ending br tags with a single newline character <br>
     * 4. Replaces ending p, div and heading tags with two newlines characters;
     * resulting in a single empty line btw. paragraphs. <br>
     * 5. Strips remaining html tags. <br>
     * <br>
     * NB: The tag stripping is done using a very simple regular expression,
     * which removes everything between &lt and &gt. Therefore too much text
     * could in some (hopefully rare!?) cases be removed.
     * 
     * @param s
     *            Input string
     * @return Input stripped for html tags
     */
    public static String stripHtmlTags(String s) {
        // initial check of input:
        if (s == null) {
            return null;
        }
        s = removeComments(s);
        // remove header
        s = HEADER_REMOVAL_PATTERN.matcher(s).replaceAll("");

        // remove extra whitespace
        s = WHITE_SPACE_REMOVAL_PATTERN.matcher(s).replaceAll(" ");

        // replace br, p and heading tags with newlines
        s = BREAK_TO_NL_PATTERN.matcher(s).replaceAll("\n");
        s = P_TO_DOUBLE_NL_PATTERN.matcher(s).replaceAll("\n\n");
        s = DIV_TO_DOUBLE_NL_PATTERN.matcher(s).replaceAll("\n\n");
        s = H_TO_DOUBLE_NL_PATTERN.matcher(s).replaceAll("\n\n");

        // strip remaining tags
        s = STRIP_TAGS_PATTERN.matcher(s).replaceAll("");

        // tag stripping can leave some double spaces at line beginnings
        s = TRIM_SPACE_PATTERN.matcher(s).replaceAll("\n").trim();

        return s;
    }

    /**
     * Strips html tags. The method used is very simple: Everything between
     * tag-start (&lt) and tag-end (&gt) is removed. Optionaly br tags are
     * replaced by newline and ending p tags with double newline.
     * 
     * @param s
     *            input string
     * @param breakToNl
     *            if true, newlines are inserted for br and p tags
     * @return output without html tags (null on error)
     * 
     * @deprecated Please use the more advanced and correct
     * @see stripHtmlTags(String) method
     */
    public static String stripHtmlTags(String s, boolean breakToNl) {
        // initial check of input:
        if (s == null) {
            return null;
        }

        if (breakToNl) {
            // replace <br> and </br> with newline
            s = BREAK_TO_NL_PATTERN.matcher(s).replaceAll("\n");

            // replace </p> with double newline
            s = P_TO_DOUBLE_NL_PATTERN.matcher(s).replaceAll("\n\n");
        }

        // strip tags
        s = STRIP_TAGS_PATTERN.matcher(s).replaceAll("");

        return s;
    }

    /**
     * Performs in large terms the reverse of substituteSpecialCharacters
     * (though br tags are not converted to newlines, this should be handled
     * separately). More preciesly it changes special entities like amp, nbsp
     * etc. to their real counter parts: &, space etc. <br>
     * 
     * @param s
     *            input string
     * @return output with special entities replaced with their "real" counter
     *         parts (null on error)
     * 
     */
    public static String restoreSpecialCharacters(String s) {
        for (int i = 0; i < SPECIAL_ENTITIES.length; i++) {
            s.replaceAll(SPECIAL_ENTITIES[i], ENTITY_STRINGS[i]);
        }
        return s;
    }

    /**
     * Strips html tags. and replaces special entities with their "normal"
     * counter parts, e.g. <code>&gt; => ></code>.<br>
     * Calling this method is the same as calling first stripHtmlTags and then
     * restoreSpecialCharacters.
     * 
     * @param html
     *            input string
     * @return output without html tags and special entities (null on error)
     */
    public static String htmlToText(String html) {
        // stripHtmlTags called with true ~ p & br => newlines
        String text = removeComments(html);
        text = stripHtmlTags(text);

        return restoreSpecialCharacters(text);
    }

    /**
     * Replaces special chars - <,>,&,\t,\n," - with the special entities used
     * in html (amp, nbsp, ...). Then the complete text is surrounded with
     * proper html tags: Starting- and ending html tag, header section and body
     * section. The complete body section is sorround with p tags. <br>
     * This is the same as first calling substituteSpecialCharacters and then
     * add starting and ending html tags etc. <br>
     * Further more urls and email adresses are converted into links Optionally
     * a title and css definition is inserted in the html header. <br>
     * 
     * TODO : Add support for smilies and coloring of quoted text
     * 
     * @param text
     *            Text to convert to html
     * @param title
     *            Title to include in header, not used if null
     * @param css
     *            Style sheet def. to include in header, not used if null. The
     *            input shall not include the style tag
     * @return Text converted to html
     */
    public static String textToHtml(String text, String title, String css) {
        // convert special characters
        String html = HtmlParser.substituteSpecialCharacters(text);

        // parse for urls / email adresses and substite with HTML-code
        html = HtmlParser.substituteURL(html);
        html = HtmlParser.substituteEmailAddress(html);

        // insert surrounding html tags
        StringBuffer buf = new StringBuffer();
        buf.append("<html>\n");
        buf.append("<head>\n");

        if (title != null) {
            buf.append("<title>\n");
            buf.append(title);
            buf.append("\n</title>");
        }

        if (css != null) {
            buf.append("\n<style type=\"text/css\"><!-- ");
            buf.append(css);
            buf.append(" -->\n</style>\n");
        }

        buf.append("</head>\n<body>\n");
        buf.append("<div>");
        buf.append(html);
        buf.append("</div>");
        buf.append("</body>");
        buf.append("</html>");

        return buf.toString();
    }

    /**
     * Substitute special characters like: <,>,&,\t,\n," with special entities
     * used in html (amp, nbsp, ...)
     * 
     * @param s
     *            input string containing special characters
     * @return output with special characters substituted (null on error)
     */
    public static String substituteSpecialCharacters(String s) {
        StringBuffer sb = new StringBuffer(s.length());
        StringReader sr = new StringReader(s);
        BufferedReader br = new BufferedReader(sr);
        String ss = null;

        try {
            while ((ss = br.readLine()) != null) {
                int i = 0;

                while (i < ss.length()) {
                    switch (ss.charAt(i)) {
                    case '<':
                        sb.append("&lt;");
                        i++;

                        break;

                    case '>':
                        sb.append("&gt;");
                        i++;

                        break;

                    case '&':
                        sb.append("&amp;");
                        i++;

                        break;

                    case '"':
                        sb.append("&quot;");
                        i++;

                        break;

                    case ' ':

                        // sb.append("&nbsp;");
                        if (ss.substring(i).startsWith("    ")) {
                            sb.append("&nbsp; ");
                            i = i + 2;
                        } else if (ss.substring(i).startsWith("   ")) {
                            sb.append("&nbsp;&nbsp; ");
                            i = i + 3;
                        } else if (ss.substring(i).startsWith("  ")) {
                            sb.append("&nbsp; ");
                            i = i + 2;
                        } else {
                            sb.append(' ');
                            i++;
                        }

                        break;

                    case '\t':
                        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                        i++;

                        break;

                    case '\n':
                        sb.append("<br>");
                        i++;

                        break;

                    default:
                        sb.append(ss.charAt(i));
                        i++;

                        break;
                    }
                }

                sb.append("<br>\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Error substituting special characters: "
                    + e.getMessage(), e);

            return null; // error
        }

        return sb.toString();
    }

    /**
     * 
     * substitute special characters like: <,>,&,\t,\n with special entities
     * used in html <br>
     * This is the same as substituteSpecialCharacters, but here an extra
     * newline character is not inserted.
     * 
     * @param s
     *            input string containing special characters
     * @return output with special characters substituted (null on error)
     */
    public static String substituteSpecialCharactersInHeaderfields(String s) {
        StringBuffer sb = new StringBuffer(s.length());
        StringReader sr = new StringReader(s);
        BufferedReader br = new BufferedReader(sr);
        String ss = null;

        // TODO: Extend handling of special entities as in
        // restoreSpecialCharacters

        try {
            while ((ss = br.readLine()) != null) {
                int i = 0;

                while (i < ss.length()) {
                    switch (ss.charAt(i)) {
                    case '<':
                        sb.append("&lt;");
                        i++;

                        break;

                    case '>':
                        sb.append("&gt;");
                        i++;

                        break;

                    case '&':
                        sb.append("&amp;");
                        i++;

                        break;

                    case '"':
                        sb.append("&quot;");
                        i++;

                        break;

                    case '\'':
                        sb.append("&apos;");
                        i++;

                        break;

                    case ' ':

                        if (ss.substring(i).startsWith("    ")) {
                            sb.append("&nbsp; ");
                            i = i + 2;
                        } else if (ss.substring(i).startsWith("   ")) {
                            sb.append("&nbsp;&nbsp; ");
                            i = i + 3;
                        } else if (ss.substring(i).startsWith("  ")) {
                            sb.append("&nbsp; ");
                            i = i + 2;
                        } else {
                            sb.append(' ');
                            i++;
                        }

                        break;

                    case '\t':
                        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                        i++;

                        break;

                    case '\n':
                        sb.append("<br>");
                        i++;

                        break;

                    default:
                        sb.append(ss.charAt(i));
                        i++;

                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error substituting special characters: "
                    + e.getMessage(), e);

            return null; // error
        }

        return sb.toString();
    }

    /**
     * Tries to fix broken html-strings by inserting html start- and end tags if
     * missing, and by removing content after the html end tag.
     * 
     * @param input
     *            html content to be validated
     * @return content with extra tags inserted if necessary
     */
    public static String validateHTMLString(String input) {
        StringBuffer output = new StringBuffer(input);
        int index = 0;

        String lowerCaseInput = input.toLowerCase();

        // Check for missing <html> tag
        if (lowerCaseInput.indexOf("<html>") == -1) {
            if (lowerCaseInput.indexOf("<!doctype") != -1) {
                index = lowerCaseInput.indexOf("\n", lowerCaseInput
                        .indexOf("<!doctype")) + 1;
            }

            output.insert(index, "<html>");
        }

        // Check for missing </html> tag
        if (lowerCaseInput.indexOf("</html>") == -1) {
            output.append("</html>");
        }

        // remove characters after </html> tag
        index = lowerCaseInput.indexOf("</html>");

        if (lowerCaseInput.length() >= (index + 7)) {
            lowerCaseInput = lowerCaseInput.substring(0, index + 7);
        }

        return output.toString();
    }

    /**
     * parse text and transform every email-address in a HTML-conform address
     * 
     * @param s
     *            input text
     * @return text with email-adresses transformed to links (null on error)
     */
    public static String substituteEmailAddress(String s) {
        return EMAIL_PATTERN.matcher(s).replaceAll("<A HREF=mailto:$1>$1</A>");
    }

    /**
     * Transforms email-addresses into HTML just as
     * substituteEmailAddress(String), but tries to ignore email-addresses,
     * which are already links, if the ignore links flag is set. <br>
     * This extended functionality is necessary when parsing a text which is
     * already (partly) html. <br>
     * 
     * 
     * @param s
     *            input text
     * @param ignoreLinks
     *            if true link tags are ignored. This gives a wrong result if
     *            some e-mail adresses are already links (but uses reg. expr.
     *            directly, and is therefore faster)
     * @return text with email-adresses transformed to links
     */
    public static String substituteEmailAddress(String s, boolean ignoreLinks) {
        if (ignoreLinks) {
            // Do not take existing link tags into account
            return substituteEmailAddress(s);
        }

        logger.info("Source:\n" + s);

        // initialisation
        Matcher noLinkMatcher = EMAIL_PATTERN.matcher(s);
        Matcher withLinkMatcher = EMAIL_PATTERN_INC_LINK.matcher(s);
        int pos = 0; // current position in s
        int length = s.length();
        StringBuffer buf = new StringBuffer();

        while (pos < length) {
            if (noLinkMatcher.find(pos)) {
                // an email adress was found - check whether its already a link
                int s1 = noLinkMatcher.start();
                int e1 = noLinkMatcher.end();
                boolean insertLink;

                if (withLinkMatcher.find(pos)) {
                    // found an email address with links - is it the same?
                    int s2 = withLinkMatcher.start();
                    int e2 = withLinkMatcher.end();

                    if ((s2 < s1) && (e2 > e1)) {
                        // same email adress - just append and continue
                        buf.append(s.substring(pos, e2));
                        pos = e2;
                        insertLink = false; // already handled
                    } else {
                        // not the same
                        insertLink = true;
                    }
                } else {
                    // no match with link tags
                    insertLink = true;
                }

                // shall we insert a link?
                if (insertLink) {
                    String email = s.substring(s1, e1);
                    String link = "<a href=\"mailto:" + email + "\">" + email
                            + "</a>";
                    buf.append(s.substring(pos, s1));
                    buf.append(link);
                    pos = e1;
                }
            } else {
                // no more matches - append rest of string
                buf.append(s.substring(pos));
                pos = length;
            }
        }

        // return result
        String result = buf.toString();
        logger.info("Result:\n" + result);

        return result;
    }

    /**
     * parse text and transform every url in a HTML-conform url
     * 
     * @param s
     *            input text
     * @return text with urls transformed to links (null on error)
     */
    public static String substituteURL(String s) {
        String match;
        Matcher m = URL_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            match = m.group();
            match = URL_REPAIR_PATTERN.matcher(match).replaceAll(
                    "<A HREF=\"$1\">$1</A>$2");
            m.appendReplacement(sb, match);
        }

        m.appendTail(sb);

        return sb.toString();
    }

    /**
     * parse text and transform every url in a HTML-conform url
     * 
     * @param s
     *            input text
     * @return text with urls transformed to links (null on error)
     */
    public static String substituteImgSrc(String s) {
        String match;
        Matcher m = SRC_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            match = m.group();
            match = SRC_REPAIR_PATTERN.matcher(match).replaceAll("") + "\"";
            m.appendReplacement(sb, match);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Transforms urls into HTML just as substituteURL(String), but tries to
     * ignore urls, which are already links, if the ignore links flag is set.
     * <br>
     * This extended functionality is necessary when parsing a text which is
     * already (partly) html. <br>
     * FIXME: Can this be done smarter, i.e. directly with reg. expr. without
     * manual parsing??
     * 
     * @param s
     *            input text
     * @param ignoreLinks
     *            if true link tags are ignored. This gives a wrong result if
     *            some urls are already links (but uses reg. expr. directly, and
     *            is therefore faster)
     * @return text with urls
     */
    public static String substituteURL(String s, boolean ignoreLinks) {
        if (ignoreLinks) {
            // Do not take existing link tags into account
            return substituteURL(s);
        }

        logger.info("Source:\n" + s);

        // initialisation
        Matcher noLinkMatcher = URL_PATTERN.matcher(s);
        Matcher withLinkMatcher = URL_PATTERN_INC_LINK.matcher(s);
        int pos = 0; // current position in s
        int length = s.length();
        StringBuffer buf = new StringBuffer();

        while (pos < length) {
            if (noLinkMatcher.find(pos)) {
                // an url - check whether its already a link
                int s1 = noLinkMatcher.start();
                int e1 = noLinkMatcher.end();
                boolean insertLink;

                if (withLinkMatcher.find(pos)) {
                    // found an url with links - is it the same?
                    int s2 = withLinkMatcher.start();
                    int e2 = withLinkMatcher.end();

                    if ((s2 < s1) && (e2 > e1)) {
                        // same url - just append and continue
                        buf.append(s.substring(pos, e2));
                        pos = e2;
                        insertLink = false; // already handled
                    } else {
                        // not the same
                        insertLink = true;
                    }
                } else {
                    // no match with link tags
                    insertLink = true;
                }

                // shall we insert a link?
                if (insertLink) {
                    String url = s.substring(s1, e1);
                    String link = "<a href=\"" + url + "\">" + url + "</a>";
                    buf.append(s.substring(pos, s1));
                    buf.append(link);
                    pos = e1;
                }
            } else {
                // no more matches - append rest of string
                buf.append(s.substring(pos));
                pos = length;
            }
        }

        // return result
        String result = buf.toString();
        logger.info("Result:\n" + result);

        return result;
    }

    /**
     * Extracts the body of a html document, i.e. the html contents between (and
     * not including) body start and end tags.
     * 
     * @param html
     *            The html document to extract the body from
     * @return The body of the html document
     * 
     */
    public static String getHtmlBody(String html) {
        // locate body start- and end tags
        String lowerCaseContent = html.toLowerCase();
        int tagStart = lowerCaseContent.indexOf("<body");

        // search for closing bracket separately to account for attributes in
        // tag
        int tagStartClose = lowerCaseContent.indexOf(">", tagStart) + 1;
        int tagEnd = lowerCaseContent.indexOf("</body>");

        // correct limits if body tags where not found
        if (tagStartClose < 0) {
            tagStartClose = 0;
        }

        if ((tagEnd < 0) || (tagEnd > lowerCaseContent.length())) {
            tagEnd = lowerCaseContent.length();
        }

        // return body
        return html.substring(tagStartClose, tagEnd);
    }

    /**
     * Parses a html documents and removes all html comments found.
     * 
     * @param html
     *            The html document
     * @return Html document without comments
     * 
     */
    public static String removeComments(String html) {
        // remove comments
        return COMMENTS_REMOVAL_PATTERN.matcher(html).replaceAll("");
    }

    public static void main(String[] args) {

        try {
            BufferedReader f = new BufferedReader(new FileReader(new File(
                    "test.html")));
            String s = null;
            StringBuffer sb = new StringBuffer();
            while ((s = f.readLine()) != null) {
                sb.append(s);
            }
            System.out.println(HtmlParser.substituteImgSrc(sb.toString()));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
