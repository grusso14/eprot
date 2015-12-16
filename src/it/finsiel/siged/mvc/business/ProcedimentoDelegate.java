package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.Procedimento;
import it.finsiel.siged.mvc.integration.ProcedimentoDAO;
import it.finsiel.siged.mvc.presentation.helper.FascicoloView;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.protocollo.FaldoneVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.StringUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class ProcedimentoDelegate {

    private static Logger logger = Logger.getLogger(ProcedimentoDelegate.class
            .getName());

    private ProcedimentoDAO procedimentoDAO = null;

    private ServletConfig config = null;

    private static ProcedimentoDelegate delegate = null;

    private ProcedimentoDelegate() {
        try {
            if (procedimentoDAO == null) {
                procedimentoDAO = (ProcedimentoDAO) DAOFactory
                        .getDAO(Constants.PROCEDIMENTO_DAO_CLASS);

                logger.debug("procedimentoDAO instantiated:"
                        + Constants.PROCEDIMENTO_DAO_CLASS);
            }
        } catch (Exception e) {
            logger.error("Exception while connecting to "
                    + Constants.PROCEDIMENTO_DAO_CLASS + "!!", e);
        }

    }

    public static ProcedimentoDelegate getInstance() {
        if (delegate == null)
            delegate = new ProcedimentoDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.PROCEDIMENTO_DELEGATE;
    }

    public ProcedimentoVO getProcedimentoByIdVersione(int procedimentoId,
            int versione) {

        try {
            ProcedimentoVO p = new ProcedimentoVO();
            p = procedimentoDAO.getProcedimentoByIdVersione(procedimentoId,
                    versione);

            return p;
        } catch (Exception de) {
            logger
                    .error("ProcedimentoDelegate: failed getting getProcedimentoById: ");
            return null;
        }
    }

    public ProcedimentoVO salvaProcedimento(Procedimento pro, Utente utente) {
        ProcedimentoVO newVO = new ProcedimentoVO();
        newVO.setReturnValue(ReturnValues.UNKNOWN);
        JDBCManager jdbcMan = null;
        Connection connection = null;
        Date dataCorrente = new Date(System.currentTimeMillis());
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            ProcedimentoVO vo = pro.getProcedimentoVO();
            if (vo.getId() != null && vo.getId().intValue() > 0) {
                vo.setRowUpdatedUser(utente.getValueObject().getUsername());
                vo.setRowUpdatedTime(new Date(System.currentTimeMillis()));
                newVO = procedimentoDAO.updateProcedimento(connection, vo);
            } else {
                vo.setAnno(DateUtil
                        .getYear(new Date(System.currentTimeMillis())));
                vo.setRowCreatedUser(utente.getValueObject().getUsername());
                vo.setRowCreatedTime(new Date(System.currentTimeMillis()));
                vo.setId(IdentificativiDelegate.getInstance().getNextId(
                        connection, NomiTabelle.PROCEDIMENTI));
                int nextNum = IdentificativiDelegate.getInstance().getNextId(
                        connection,
                        NomiTabelle.PROCEDIMENTI
                                + String.valueOf(pro.getProcedimentoVO()
                                        .getAooId())
                                + DateUtil.getYear(dataCorrente));
                // int nextNum =
                // procedimentoDAO.getMaxNumProcedimento(connection,
                // utente.getValueObject().getAooId(), DateUtil
                // .getYear(new Date(System.currentTimeMillis())));
                vo.setNumero(nextNum);
                vo.setNumeroProcedimento(String.valueOf(DateUtil
                        .getYear(new Date(System.currentTimeMillis())))
                        + StringUtil.formattaNumeroProcedimento(String
                                .valueOf(nextNum), 7));
                newVO = procedimentoDAO.newProcedimento(connection, vo);
            }
            procedimentoDAO.cancellaFaldoni(connection, vo.getId().intValue());
            procedimentoDAO
                    .cancellaFascicoli(connection, vo.getId().intValue());
            procedimentoDAO.cancellaProtocolli(connection, vo.getId()
                    .intValue());
            if (!pro.getFascicoli().isEmpty()) {
                ArrayList ids = new ArrayList();
                Iterator it = pro.getFascicoli().values().iterator();
                while (it.hasNext()) {
                    ids.add(new Integer(((FascicoloView) it.next()).getId()));
                }
                procedimentoDAO.inserisciFascicoli(connection, (Integer[]) ids
                        .toArray(new Integer[0]), vo.getId().intValue());
            }
            if (!pro.getFaldoni().isEmpty()) {
                ArrayList ids = new ArrayList();
                Iterator it = pro.getFaldoni().values().iterator();
                while (it.hasNext()) {
                    ids.add(new Integer(((FaldoneVO) it.next()).getId()
                            .intValue()));
                }
                procedimentoDAO.inserisciFaldoni(connection, (Integer[]) ids
                        .toArray(new Integer[0]), vo.getId().intValue());
            }
            if (!pro.getProtocolli().isEmpty()) {
                ArrayList ids = new ArrayList();
                Iterator it = pro.getProtocolli().values().iterator();
                while (it.hasNext()) {
                    ProtocolloProcedimentoVO protocolloProcedimentoVO = new ProtocolloProcedimentoVO();
                    ReportProtocolloView reportProtocolloView = (ReportProtocolloView) it
                            .next();
                    protocolloProcedimentoVO.setProcedimentoId(vo.getId()
                            .intValue());
                    protocolloProcedimentoVO
                            .setProtocolloId(reportProtocolloView
                                    .getProtocolloId());
                    protocolloProcedimentoVO.setVersione(reportProtocolloView
                            .getVersione());
                    protocolloProcedimentoVO.setRowCreatedUser(utente
                            .getValueObject().getUsername());
                    protocolloProcedimentoVO.setRowUpdatedUser(utente
                            .getValueObject().getUsername());
                    protocolloProcedimentoVO.setRowCreatedTime(new Date(System
                            .currentTimeMillis()));
                    protocolloProcedimentoVO.setRowUpdatedTime(new Date(System
                            .currentTimeMillis()));
                    ids.add(protocolloProcedimentoVO);
                }
                procedimentoDAO.inserisciProtocolli(connection, ids);
            }
            ProtocolloVO p = ProtocolloDelegate.getInstance()
                    .getProtocolloById(vo.getProtocolloId());
            if (p.getFlagTipo().equals("I")) {
                procedimentoDAO.setStatoProtocolloAssociato(connection, vo
                        .getProtocolloId(),
                        Parametri.STATO_ASSOCIATO_A_PROCEDIMENTO);
            }
            ReportProtocolloView pa = ProtocolloDelegate.getInstance()
                    .getProtocolloView(connection, vo.getProtocolloId());
            newVO.setNumeroProtovollo(pa.getAnnoNumeroProtocollo());
            connection.commit();
            newVO.setReturnValue(ReturnValues.SAVED);
        } catch (DataException de) {
            newVO.setReturnValue(ReturnValues.UNKNOWN);
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Procedimento fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            newVO.setReturnValue(ReturnValues.UNKNOWN);
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Procedimento fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            newVO.setReturnValue(ReturnValues.UNKNOWN);
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return newVO;
    }

    // STORIA PROCEDIMENTO

    public Collection getStoriaProcedimento(int procedimentoId) {
        try {
            return procedimentoDAO.getStoriaProcedimenti(procedimentoId);
        } catch (DataException de) {
            logger
                    .error("StoriaProcedimentoDelegate: failed getting getStoriaProcedimento: ");
            return null;
        }
    }

    public ProcedimentoVO aggiornaProcedimento(Procedimento pro, Utente utente) {
        ProcedimentoVO newVO = new ProcedimentoVO();
        newVO.setReturnValue(ReturnValues.UNKNOWN);
        JDBCManager jdbcMan = null;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            ProcedimentoVO vo = pro.getProcedimentoVO();

            if (vo.getId() != null && vo.getId().intValue() > 0) {
                vo.setRowUpdatedUser(utente.getValueObject().getUsername());
                vo.setRowUpdatedTime(new Date(System.currentTimeMillis()));
                newVO = procedimentoDAO.aggiornaProcedimento(connection, vo);
            } else {
                vo.setAnno(DateUtil
                        .getYear(new Date(System.currentTimeMillis())));
                vo.setRowCreatedUser(utente.getValueObject().getUsername());
                vo.setRowCreatedTime(new Date(System.currentTimeMillis()));
                vo.setId(IdentificativiDelegate.getInstance().getNextId(
                        connection, NomiTabelle.PROCEDIMENTI));
                int nextNum = procedimentoDAO.getMaxNumProcedimento(connection,
                        utente.getValueObject().getAooId(), DateUtil
                                .getYear(new Date(System.currentTimeMillis())));
                vo.setNumero(nextNum);
                vo.setNumeroProcedimento(String.valueOf(DateUtil
                        .getYear(new Date(System.currentTimeMillis())))
                        + StringUtil.formattaNumeroFaldone(String
                                .valueOf(nextNum), 6));
                newVO = procedimentoDAO.newProcedimento(connection, vo);
            }
            procedimentoDAO.cancellaFaldoni(connection, vo.getId().intValue());
            procedimentoDAO
                    .cancellaFascicoli(connection, vo.getId().intValue());
            procedimentoDAO.cancellaProtocolli(connection, vo.getId()
                    .intValue());
            if (!pro.getFascicoli().isEmpty()) {
                ArrayList ids = new ArrayList();
                Iterator it = pro.getFascicoli().values().iterator();
                while (it.hasNext()) {
                    ids.add(new Integer(((FascicoloView) it.next()).getId()));
                }
                procedimentoDAO.inserisciFascicoli(connection, (Integer[]) ids
                        .toArray(new Integer[0]), vo.getId().intValue());
            }
            if (!pro.getFaldoni().isEmpty()) {
                ArrayList ids = new ArrayList();
                Iterator it = pro.getFaldoni().values().iterator();
                while (it.hasNext()) {
                    ids.add(new Integer(((FaldoneVO) it.next()).getId()
                            .intValue()));
                }
                procedimentoDAO.inserisciFaldoni(connection, (Integer[]) ids
                        .toArray(new Integer[0]), vo.getId().intValue());
            }
            if (!pro.getProtocolli().isEmpty()) {
                ArrayList ids = new ArrayList();
                Iterator it = pro.getProtocolli().values().iterator();
                while (it.hasNext()) {
                    ProtocolloProcedimentoVO protocolloProcedimentoVO = new ProtocolloProcedimentoVO();
                    ReportProtocolloView reportProtocolloView = (ReportProtocolloView) it
                            .next();
                    protocolloProcedimentoVO.setProcedimentoId(vo.getId()
                            .intValue());
                    protocolloProcedimentoVO
                            .setProtocolloId(reportProtocolloView
                                    .getProtocolloId());
                    protocolloProcedimentoVO.setVersione(reportProtocolloView
                            .getVersione());
                    protocolloProcedimentoVO.setRowCreatedUser(utente
                            .getValueObject().getUsername());
                    protocolloProcedimentoVO.setRowUpdatedUser(utente
                            .getValueObject().getUsername());
                    protocolloProcedimentoVO.setRowCreatedTime(new Date(System
                            .currentTimeMillis()));
                    protocolloProcedimentoVO.setRowUpdatedTime(new Date(System
                            .currentTimeMillis()));
                    ids.add(protocolloProcedimentoVO);
                }
                procedimentoDAO.inserisciProtocolli(connection, ids);
            }
            ProtocolloVO p = ProtocolloDelegate.getInstance()
                    .getProtocolloById(vo.getProtocolloId());
            if (p.getFlagTipo().equals("I")) {
                procedimentoDAO.setStatoProtocolloAssociato(connection, vo
                        .getProtocolloId(),
                        Parametri.STATO_ASSOCIATO_A_PROCEDIMENTO);
            }
            ReportProtocolloView pa = ProtocolloDelegate.getInstance()
                    .getProtocolloView(connection, vo.getProtocolloId());
            newVO.setNumeroProtovollo(pa.getAnnoNumeroProtocollo());
            connection.commit();
            newVO.setReturnValue(ReturnValues.SAVED);
        } catch (DataException de) {
            newVO.setReturnValue(ReturnValues.UNKNOWN);
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Procedimento fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            newVO.setReturnValue(ReturnValues.UNKNOWN);
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Procedimento fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            newVO.setReturnValue(ReturnValues.UNKNOWN);
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return newVO;
    }

    public int contaProcedimenti(Utente utente, HashMap sqlDB) {
        try {

            return procedimentoDAO.contaProcedimenti(utente, sqlDB);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting contaProtocolli: ");
            return 0;
        }

    }

    public SortedMap cercaProcedimenti(Utente utente, HashMap sqlDB) {
        try {

            return procedimentoDAO.cerca(utente, sqlDB);
        } catch (DataException de) {
            logger.error("failed getting cercaProcedimenti: ");
            return null;
        }

    }

    public ProcedimentoVO getProcedimentoVO(int id) {
        try {
            return procedimentoDAO.getProcedimentoById(id);
        } catch (DataException de) {
            logger.error("failed getting ProcedimentoVO: " + id);
            return null;
        }

    }

    public Procedimento getProcedimentoById(int procedimentoId) {
        Procedimento p = new Procedimento();
        JDBCManager jdbcMan = null;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            ProcedimentoVO vo = procedimentoDAO.getProcedimentoById(connection,
                    procedimentoId);
            if (vo.getReturnValue() == ReturnValues.FOUND) {
                ReportProtocolloView pa = ProtocolloDelegate.getInstance()
                        .getProtocolloView(connection, vo.getProtocolloId());
                vo.setNumeroProtovollo(pa.getAnnoNumeroProtocollo());
                p.setProcedimentoVO(vo);
                // carico faldoni associati
                Collection idf = procedimentoDAO.getProcedimentoFaldoni(
                        connection, procedimentoId);
                Iterator itf = idf.iterator();
                while (itf.hasNext()) {
                    Integer id = (Integer) itf.next();
                    FaldoneVO f = FaldoneDelegate.getInstance().getFaldone(
                            connection, id.intValue());
                    if (f.getReturnValue() == ReturnValues.FOUND)
                        p.getFaldoni().put(String.valueOf(f.getId()), f);
                }
                // carico fascicoli associati
                Collection idfa = procedimentoDAO.getProcedimentoFascicoli(
                        connection, procedimentoId);
                Iterator itfa = idfa.iterator();
                while (itfa.hasNext()) {
                    Integer id = (Integer) itfa.next();
                    FascicoloView f = FascicoloDelegate.getInstance()
                            .getFascicoloViewById(connection, id.intValue());
                    if (f != null)
                        p.getFascicoli().put(String.valueOf(f.getId()), f);
                }
                // carico protocolli associati
                Collection idp = procedimentoDAO.getProcedimentoProtocolli(
                        connection, procedimentoId);
                Iterator itp = idp.iterator();
                while (itp.hasNext()) {
                    Integer id = (Integer) itp.next();
                    ReportProtocolloView f = ProtocolloDelegate.getInstance()
                            .getProtocolloView(connection, id.intValue());
                    if (vo.getProtocolloId() == f.getProtocolloId()) {
                        f.setModificabile(false);
                    } else {
                        f.setModificabile(true);
                    }

                    if (f != null)
                        p.getProtocolli().put(
                                String.valueOf(f.getProtocolloId()), f);
                }
            } else {
                p = null;
            }
        } catch (Exception e) {
            logger.error("errore", e);
        } finally {
            jdbcMan.close(connection);
        }
        return p;
    }
}