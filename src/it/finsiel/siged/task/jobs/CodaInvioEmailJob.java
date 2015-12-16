/*
 * Created on 24-mar-2005
 *
 */
package it.finsiel.siged.task.jobs;

import it.finsiel.siged.constant.EmailConstants;
import it.finsiel.siged.dao.mail.PecEmailUscita;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.mvc.business.EmailDelegate;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;

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
public class CodaInvioEmailJob implements Serializable, StatefulJob {

    static Logger logger = Logger.getLogger(CodaInvioEmailJob.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */

    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        logger.info("Invio messaggi protocollo in ustita tramite PEC in corso per AOO:\n"
                        + context.getJobDetail().getFullName());
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int aooId = dataMap.getInt("aoo_id");
        String tempFolder = dataMap.getString("mail.tempfolder");
        AreaOrganizzativaVO aooVO = Organizzazione.getInstance()
                .getAreaOrganizzativa(aooId).getValueObject();
        String host = aooVO.getPec_smtp();
        String port = aooVO.getPec_smtp_port();
        String authentication = "true";
        String username = aooVO.getPec_username();
        String password = aooVO.getPec_pwd();
        String emailMittente = aooVO.getPec_indirizzo();

        // execute email task

        try {
            PecEmailUscita.inviaProtocolliUscita(EmailDelegate.getInstance(),
                    aooId, host, port, username, password, emailMittente,
                    authentication, tempFolder);
        } catch (Exception e) {
            EmailDelegate.getInstance().salvaEmailLog(
                    "Invio Protocolli in uscita non riuscito.",
                    "Errore generale", e.getMessage(),
                    EmailConstants.LOG_EMAIL_USCITA, aooId);
            logger.error("", e);
        }

    }

}
