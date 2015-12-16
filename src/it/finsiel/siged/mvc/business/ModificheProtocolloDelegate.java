package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.ModificheProtocolloDAO;

import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class ModificheProtocolloDelegate {

    private static Logger logger = Logger
            .getLogger(ModificheProtocolloDelegate.class.getName());

    private ModificheProtocolloDAO dao = null;

    private ServletConfig config = null;

    private static ModificheProtocolloDelegate delegate = null;

    public static String getIdentifier() {
        return Constants.MODIFICHEPROTOCOLLO_DELEGATE;
    }

    private ModificheProtocolloDelegate() {
        try {
            if (dao == null) {
                dao = (ModificheProtocolloDAO) DAOFactory
                        .getDAO(Constants.MODIFICHEPROTOCOLLO_DAO_CLASS);

                logger.debug("protocolloDAO instantiated:"
                        + Constants.MODIFICHEPROTOCOLLO_DAO_CLASS);
            }
        } catch (Exception e) {
            logger.error("Exception while connecting to UserDAOjdbc!!", e);
        }
    }

    public static ModificheProtocolloDelegate getInstance() {
        if (delegate == null)
            delegate = new ModificheProtocolloDelegate();
        return delegate;
    }

    public List getModificheProtocollo(int registroId, int ufficioId,
            long dataInizio, long dataFine) {
        List elenco = null;
        try {
            elenco = dao.getModifiche(registroId, ufficioId, dataInizio,
                    dataFine);

        } catch (DataException de) {
            logger.error("ModificheProtocolloDelegate: "
                    + "failed getting getModificheProtocollo: ", de);
        }
        return elenco;
    }

}