package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Menu;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.AreaOrganizzativaDAO;
import it.finsiel.siged.mvc.plugin.PersistencePlugIn;
import it.finsiel.siged.mvc.vo.lookup.TipoDocumentoVO;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;
import it.finsiel.siged.mvc.vo.organizzazione.MenuVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.SpedizioneVO;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.FileUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class AreaOrganizzativaDelegate implements ComponentStatus {

    private static Logger logger = Logger
            .getLogger(AreaOrganizzativaDelegate.class.getName());

    private int status;

    private AreaOrganizzativaDAO areaorganizzativaDAO = null;

    private ServletConfig config = null;

    private static AreaOrganizzativaDelegate delegate = null;

    private AreaOrganizzativaDelegate() {
        try {
            if (areaorganizzativaDAO == null) {
                config = PersistencePlugIn.servletConfig;
                areaorganizzativaDAO = (AreaOrganizzativaDAO) DAOFactory
                        .getDAO(Constants.AREAORGANIZZATIVA_DAO_CLASS);

                logger.debug("AreaOrganizzativaDAO instantiated: " + Constants.AREAORGANIZZATIVA_DAO_CLASS);
                status = STATUS_OK;
            }
        } catch (Exception e) {
            status = STATUS_ERROR;
            logger.error("", e);
        }

    }

    public static AreaOrganizzativaDelegate getInstance() {
        if (delegate == null)
            delegate = new AreaOrganizzativaDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.AREAORGANIZZATIVA_DELEGATE;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int s) {
        this.status = s;
    }

    // fine metodi interfaccia

    public Collection getAreeOrganizzative() throws DataException {
        return areaorganizzativaDAO.getAreeOrganizzative();
    }

    public AreaOrganizzativaVO getAreaOrganizzativa(int areaorganizzativaId) {
        try {
            return areaorganizzativaDAO
                    .getAreaOrganizzativa(areaorganizzativaId);
        } catch (DataException de) {
            logger
                    .error("AreaOrganizzativaDelegate: failed getting getAreaOrganizzativa: ");
            return null;
        }
    }

    // Luigi 01/02/2006
    public Collection getUffici(int areaorganizzativaId) {
        try {
            return areaorganizzativaDAO.getUffici(areaorganizzativaId);
        } catch (DataException de) {
            logger
                    .error("AreaOrganizzativaDelegate: failed getting getUffici: ");
            return null;
        }
    }

    public AreaOrganizzativaVO salvaAreaOrganizzativa(
            AreaOrganizzativaVO areaorganizzativaVO, Utente utente) {
        AreaOrganizzativaVO aooSalvata = new AreaOrganizzativaVO();
        JDBCManager jdbcMan = null;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            if (areaorganizzativaVO.getId() != null
                    && areaorganizzativaVO.getId().intValue() > 0) {
                aooSalvata = areaorganizzativaDAO.updateAreaOrganizzativa(
                        connection, areaorganizzativaVO);
            } else {
                areaorganizzativaVO.setId(IdentificativiDelegate.getInstance()
                        .getNextId(connection, NomiTabelle.AREE_ORGANIZZATIVE));
                int ammId = Organizzazione.getInstance().getValueObject()
                        .getId().intValue();
                areaorganizzativaVO.setAmministrazione_id(ammId);

                aooSalvata = areaorganizzativaDAO.newAreaOrganizzativa(
                        connection, areaorganizzativaVO);

                /*
                 * Per ogni nuova Area Organizzativa Omogenea vengono creati
                 * automaticamente - un registro Ufficiale della AOO - un
                 * ufficio root - un utente admin con accesso al menu
                 * amministrazione.
                 */

                // Registro della AOO
                RegistroVO newReg = RegistroDelegate.getInstance()
                        .salvaRegistro(connection,
                                getRegistroUfficialeAOO(areaorganizzativaVO));
                String[] aRegistri = new String[1];
                aRegistri[0] = newReg.getId().toString();

                // Ufficio root della AOO
                UfficioVO newUff = UfficioDelegate.getInstance().salvaUfficio(
                        connection, getUfficioRootAOO(areaorganizzativaVO));
                String[] aUffici = new String[1];
                aUffici[0] = newUff.getId().toString();

                // Funzioni di menu
                int i = 0;
                Organizzazione org = Organizzazione.getInstance();
                String[] aMenu = new String[org.getMenuList().size()];
                for (Iterator iter = org.getMenuList().iterator(); iter
                        .hasNext();) {
                    MenuVO menu = ((Menu) iter.next()).getValueObject();
                    aMenu[i++] = menu.getId().toString();
                }

                // Utente amministratore
                UtenteDelegate.getInstance().newUtenteVO(
                        connection, getUtenteAmm(areaorganizzativaVO), aUffici,
                        aMenu, aRegistri, utente);

                // Mezzo di spedizione Email non cancellabile, utilizzato in
                // Protocollo Email
                AmministrazioneDelegate.getInstance().salvaMezzoSpedizione(
                        connection, getMezzoEmail(areaorganizzativaVO));

                // Tipo documento di default
                AmministrazioneDelegate.getInstance().salvaTipoDocumento(
                        connection, getTipoDocumento(areaorganizzativaVO));

            }
            Organizzazione org = Organizzazione.getInstance();
            String dirDocAcquisizioneMassiva = org.getValueObject()
                    .getPathDoc()
                    + "/" + "aoo_" + String.valueOf(aooSalvata.getId());

            if (FileUtil.gestionePathDoc(dirDocAcquisizioneMassiva) != ReturnValues.INVALID) {
                aooSalvata.setReturnValue(ReturnValues.SAVED);
                connection.commit();
            }
        } catch (DataException de) {
            aooSalvata.setReturnValue(ReturnValues.UNKNOWN);
            jdbcMan.rollback(connection);
            logger.warn("Salvataggio AOO fallito, rolling back transction..",
                    de);

        } catch (SQLException se) {
            aooSalvata.setReturnValue(ReturnValues.UNKNOWN);
            jdbcMan.rollback(connection);
            logger.warn("Salvataggio AOO fallito, rolling back transction..",
                    se);

        } catch (Exception e) {
            aooSalvata.setReturnValue(ReturnValues.UNKNOWN);
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return aooSalvata;
    }

    public boolean isModificabileDipendenzaTitolarioUfficio(int aooId) {
        boolean esiste = false;
        try {
            esiste = areaorganizzativaDAO
                    .isModificabileDipendenzaTitolarioUfficio(aooId);

        } catch (Exception de) {
            logger.error("AreaOrganizzativaDelegate: failed cancellaUfficio: ");
        }
        return esiste;
    }

    public boolean esisteAreaOrganizzativa(String descrizioneAoo, int aooId) {
        boolean esiste = false;
        try {
            esiste = areaorganizzativaDAO.esisteAreaOrganizzativa(
                    descrizioneAoo, aooId);

        } catch (Exception de) {
            logger.error("AreaOrganizzativaDelegate: failed cancellaUfficio: ");
        }
        return esiste;
    }

    public boolean cancellaAreaOrganizzativa(int aoo_Id) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        boolean cancellato = false;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            if (aoo_Id > 0
                    && areaorganizzativaDAO
                            .isAreaOrganizzativaCancellabile(aoo_Id)) {
                areaorganizzativaDAO.cancellaAreaOrganizzativa(connection,
                        aoo_Id);
                connection.commit();
                cancellato = true;
            }

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("AreaOrganizzativaDelegate: failed cancellaUfficio: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return cancellato;
    }

    private RegistroVO getRegistroUfficialeAOO(
            AreaOrganizzativaVO areaorganizzativaVO) {
        RegistroVO regUff = new RegistroVO();
        regUff.setId(0);
        regUff.setAooId(areaorganizzativaVO.getId().intValue());
        regUff.setDataAperturaRegistro(new Date(System.currentTimeMillis()));
        regUff.setCodRegistro("RegUff");
        regUff.setDescrizioneRegistro("Registro Ufficiale");
        regUff.setDataBloccata(false);
        regUff.setUfficiale(true);
        regUff.setApertoIngresso(true);
        regUff.setApertoUscita(true);
        regUff.setRowCreatedUser(areaorganizzativaVO.getRowCreatedUser());
        regUff.setRowUpdatedTime(new Date(System.currentTimeMillis()));
        return regUff;
    }

    private UfficioVO getUfficioRootAOO(AreaOrganizzativaVO areaorganizzativaVO) {

        UfficioVO uff = new UfficioVO();
        uff.setId(0);
        uff.setAooId(areaorganizzativaVO.getId().intValue());
        uff.setDescription("Ufficio root - "
                + areaorganizzativaVO.getDescription());
        uff.setAttivo(true);
        uff.setTipo("C");
        uff.setAccettazioneAutomatica(false);
        uff.setAooId(areaorganizzativaVO.getId().intValue());
        uff.setRowCreatedUser(areaorganizzativaVO.getRowCreatedUser());
        uff.setRowUpdatedTime(new Date(System.currentTimeMillis()));
        return uff;
    }

    private UtenteVO getUtenteAmm(AreaOrganizzativaVO areaorganizzativaVO) {

        UtenteVO ute = new UtenteVO();
        ute.setId(0);
        ute.setAooId(areaorganizzativaVO.getId().intValue());
        ute.setUsername("admin" + areaorganizzativaVO.getId());
        ute.setPassword("admin" + areaorganizzativaVO.getId());
        ute.setCognome(areaorganizzativaVO.getDescription());
        ute.setNome(areaorganizzativaVO.getDescription());
        ute.setCodiceFiscale("1");
        ute.setEmailAddress(null);
        ute.setMatricola(null);
        ute.setAbilitato(true);
        ute.setDataFineAttivita(null);
        ute.setRowCreatedUser(areaorganizzativaVO.getRowCreatedUser());
        ute.setRowUpdatedTime(new Date(System.currentTimeMillis()));
        return ute;
    }

    private SpedizioneVO getMezzoEmail(AreaOrganizzativaVO areaorganizzativaVO) {

        SpedizioneVO mezzoEmail = new SpedizioneVO();
        mezzoEmail.setAooId(areaorganizzativaVO.getId().intValue());
        mezzoEmail.setCodiceSpedizione("Email");
        mezzoEmail.setDescrizioneSpedizione("Email");
        mezzoEmail.setFlagAbilitato(true);
        mezzoEmail.setFlagCancellabile(false);
        mezzoEmail.setRowCreatedUser(areaorganizzativaVO.getRowCreatedUser());
        mezzoEmail.setRowUpdatedTime(new Date(System.currentTimeMillis()));
        return mezzoEmail;
    }

    private TipoDocumentoVO getTipoDocumento(
            AreaOrganizzativaVO areaorganizzativaVO) {

        TipoDocumentoVO tipoDoc = new TipoDocumentoVO();
        tipoDoc.setId(0);
        tipoDoc.setAooId(areaorganizzativaVO.getId().intValue());
        tipoDoc.setFlagAttivazione("1");
        tipoDoc.setFlagDefault("1");
        tipoDoc.setDescrizioneDoc("Lettera");
        tipoDoc.setRowCreatedUser(areaorganizzativaVO.getRowCreatedUser());
        tipoDoc.setRowUpdatedTime(new Date(System.currentTimeMillis()));
        return tipoDoc;
    }

}