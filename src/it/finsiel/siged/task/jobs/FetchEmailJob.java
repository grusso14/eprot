/*
 * Created on 24-mar-2005
 *
 */
package it.finsiel.siged.task.jobs;

import it.finsiel.siged.constant.EmailConstants;
import it.finsiel.siged.dao.mail.PecEmailIngresso;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.mvc.business.EmailDelegate;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;
import it.finsiel.siged.util.FileUtil;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

/**
 * @author Almaviva sud
 * 
 */
public class FetchEmailJob implements Serializable, StatefulJob {

    static Logger logger = Logger.getLogger(FetchEmailJob.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        logger.info("Controllo della casella email per protocollo in ingresso su PEC in corso per AOO:\n"
                        + context.getJobDetail().getFullName());
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int aooId = dataMap.getInt("aoo_id");
        String tempFolder = dataMap.getString("mail.tempfolder");
        AreaOrganizzativaVO aooVO = Organizzazione.getInstance()
                .getAreaOrganizzativa(aooId).getValueObject();

        String host = aooVO.getPec_pop3();
        String port = aooVO.getPec_ssl_port();
        String authentication = "false";
        String username = aooVO.getPec_username();
        String password = aooVO.getPec_pwd();
        // elimina tutti i file temporanei
        FileUtil.deltree(tempFolder);
        File tmp = new File(tempFolder);
        tmp.mkdirs(); // crea la cartella temporanea.

        try {
            PecEmailIngresso.preparaProtocolliMessaggiIngresso(EmailDelegate.getInstance(), aooId, host, port, username, password,
                    authentication, tempFolder);
        } catch (Exception e) {
            logger.error("", e);
            EmailDelegate.getInstance().salvaEmailLog(
                    "Controllo dei messaggi sul server:" + host + " fallito!",
                    "Controllo Casella di Posta", e.getMessage(),
                    EmailConstants.LOG_EMAIL_INGRESSO, aooId);
        }
    }

}
