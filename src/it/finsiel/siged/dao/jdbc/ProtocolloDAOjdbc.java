package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.business.DocumentoDelegate;
import it.finsiel.siged.mvc.business.IdentificativiDelegate;
import it.finsiel.siged.mvc.integration.ProtocolloDAO;
import it.finsiel.siged.mvc.presentation.helper.AllaccioView;
import it.finsiel.siged.mvc.presentation.helper.MittenteView;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.protocollo.AllaccioVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.mvc.vo.protocollo.SegnaturaVO;
import it.finsiel.siged.mvc.vo.protocollo.SpedizioneDestinatarioVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.StringUtil;
import it.flosslab.dao.helper.ProtocolloDaoHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class ProtocolloDAOjdbc implements ProtocolloDAO {
    static Logger logger = Logger.getLogger(ProtocolloDAOjdbc.class.getName());

    private JDBCManager jdbcMan = new JDBCManager();

    public ProtocolloVO getProtocolloById(int id) throws DataException {
        ProtocolloVO protOut = new ProtocolloVO();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            protOut = getProtocolloById(connection, id);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " ProtocolloId :" + id);
        } finally {
            jdbcMan.close(connection);
        }
        return protOut;
    }

    public ReportProtocolloView getProtocolloView(int id) throws DataException {
        ReportProtocolloView protOut = new ReportProtocolloView();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            protOut = getProtocolloView(connection, id);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " ProtocolloId :" + id);
        } finally {
            jdbcMan.close(connection);
        }
        return protOut;
    }

    public ReportProtocolloView getProtocolloView(Connection connection, int id)
            throws DataException {
        ReportProtocolloView protocollo = new ReportProtocolloView();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("getProtocolloView() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(SELECT_PROTOCOLLO_BY_ID);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	ProtocolloDaoHelper.fillReportProtocolloViewFromRecord(protocollo, rs);
            } else {
                protocollo = null;
            }
        } catch (Exception e) {
            logger.error("Load ProtocolloView by ID", e);
            throw new DataException("Cannot load ProtocolloView by ID");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return protocollo;
    }

    public ProtocolloVO getProtocolloById(Connection connection, int id)
            throws DataException {
        ProtocolloVO protocollo = new ProtocolloVO();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("getProtocolloById() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(SELECT_PROTOCOLLO_BY_ID);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ProtocolloDaoHelper.fillProtocolloVOFromRecord(protocollo, rs);
            } else {
                protocollo.setReturnValue(ReturnValues.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.error("Load Protocollo by ID", e);
            throw new DataException("Cannot load Protocollo by ID");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return protocollo;
    }

	

    private void archiviaVersione(Connection connection, int protocolloId,
            int versione) throws DataException {
        String[] tables = { "protocolli", "protocollo_allacci",
                "protocollo_allegati", "protocollo_annotazioni",
                "protocollo_assegnatari", "protocollo_destinatari",
                "protocollo_procedimenti" };
        // "protocollo_destinatari_mspediz" };

        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("storia - Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            for (int i = 0; i < tables.length; i++) {
                String sql = "INSERT INTO storia_" + tables[i]
                        + " SELECT * FROM " + tables[i]
                        + " WHERE protocollo_id = ?";
                // logger.debug(sql);
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, protocolloId);
                pstmt.executeUpdate();
                jdbcMan.close(pstmt);
                sql = "UPDATE " + tables[i] + " SET versione = ?" + " WHERE protocollo_id = ?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, versione);
                pstmt.setInt(2, protocolloId);

                pstmt.executeUpdate();
                jdbcMan.close(pstmt);
            }
            // TODO: TEST_BUG_FIX
            String tmpDel = "DELETE FROM protocollo_allegati WHERE protocollo_id = ?";
            pstmt = connection.prepareStatement(tmpDel);
            pstmt.setInt(1, protocolloId);
            pstmt.executeUpdate();
            jdbcMan.close(pstmt);
            // logger.debug("Cancellato le referenze dei documenti per protId="
            // + protocolloId + " n=" + deleted);

        } catch (Exception e) {
            logger.error("storia protocollo" + protocolloId, e);
            throw new DataException("Cannot insert Storia Protocollo");

        } finally {
            jdbcMan.close(pstmt);
        }
    }

    /*
     * Metodo per inserire un nuovo ProtocolloVO nella tabella protocollo.
     * Fornire una connessione valida, la stessa deve avere autocommit su false
     * se questo metodo deve essere parte di una transazione che potrebbe essere
     * annullata.
     * 
     * @see it.finsiel.siged.mvc.integration.ProtocolloDAO#newProtocollo(java.sql.Connection,
     *      it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO)
     * 
     */
    public ProtocolloVO newProtocollo(Connection connection,
            ProtocolloVO protocollo) throws DataException {
        ProtocolloVO newProtocollo = new ProtocolloVO();
        newProtocollo.setReturnValue(ReturnValues.UNKNOWN);
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("newProtocollo() - Invalid Connection :" + connection);
                throw new DataException("Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(INSERT_PROTOCOLLO);
            ProtocolloDaoHelper.createStatementForNewProtocollo(protocollo, pstmt);
            pstmt.executeUpdate();

            newProtocollo = getProtocolloById(connection, protocollo.getId().intValue());
            if (newProtocollo.getReturnValue() == ReturnValues.FOUND) {
                newProtocollo.setReturnValue(ReturnValues.SAVED);
                // logger.debug("Protocollo inserito:" +
                // protocollo.getId().intValue());
            } else {
                logger.warn("Errore nella lettura del ProtocolloVO dopo il salvataggio! Id:" + protocollo.getId().intValue());
                throw new DataException("Errore nel salvataggio del Protocollo.");
            }

        } catch (SQLException e) {
            logger.error("Save Protocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return newProtocollo;
    }

	

    /*
     * Metodo per inserire un nuovo ProtocolloVO nella tabella protocollo.
     * Fornire una connessione valida, la stessa deve avere autocommit su false
     * se questo metodo deve essere parte di una transazione che potrebbe essere
     * annullata.
     * 
     * @see it.finsiel.siged.mvc.integration.ProtocolloDAO#newProtocollo(java.sql.Connection,
     *      it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO)
     * 
     */
    public ProtocolloVO newStoriaProtocollo(Connection connection,
            ProtocolloVO protocollo) throws DataException {
        ProtocolloVO newProtocollo = new ProtocolloVO();
        newProtocollo.setReturnValue(ReturnValues.UNKNOWN);
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("newProtocollo() - Invalid Connection :" + connection);
                throw new DataException("Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(INSERT_STORIA_PROTOCOLLO);
            ProtocolloDaoHelper.createStatementForNewProtocollo(protocollo, pstmt);
            pstmt.executeUpdate();

            newProtocollo = getProtocolloById(connection, protocollo.getId()
                    .intValue());
            if (newProtocollo.getReturnValue() == ReturnValues.FOUND) {
                newProtocollo.setReturnValue(ReturnValues.SAVED);
                // logger.debug("Protocollo inserito:" +
                // protocollo.getId().intValue());
            } else {
                logger.warn("Errore nella lettura del ProtocolloVO dopo il salvataggio! Id:"  + protocollo.getId().intValue());
                throw new DataException("Errore nel salvataggio del Protocollo.");
            }
        } catch (SQLException e) {
            logger.error("Save Protocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return newProtocollo;
    }

    public ProtocolloVO aggiornaProtocollo(Connection connection,
            ProtocolloVO protocollo) throws DataException {
        protocollo.setReturnValue(ReturnValues.UNKNOWN);
        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("aggiornaProtocollo() - Invalid Connection :" + connection);
                throw new DataException("Connessione alla base dati non valida.");
            }
            int protocolloId = protocollo.getId().intValue();
            // archivio la versione precedente
            ProtocolloVO p = getProtocolloById(connection, protocolloId);
            if (p.getVersione() == protocollo.getVersione()) {

                archiviaVersione(connection, protocolloId, protocollo.getVersione() + 1);

                pstmt = connection.prepareStatement(UPDATE_PROTOCOLLO);

                ProtocolloDaoHelper.createStatementToUpdateProtocollo(protocollo, pstmt);

                int result = pstmt.executeUpdate();

                if (result == 1) {
                    protocollo = getProtocolloById(connection, protocolloId);
                    if (protocollo.getReturnValue() == ReturnValues.FOUND) {
                        protocollo.setReturnValue(ReturnValues.SAVED);
                        // logger.debug("Protocollo aggiornato:" +
                        // protocolloId);
                    } else {
                        logger.warn("Errore nella lettura del ProtocolloVO dopo l'aggiornamento! Id:"
                                        + protocolloId);
                        throw new DataException("Errore nell'aggiornamento del Protocollo.");
                    }
                } else {
                    protocollo.setReturnValue(ReturnValues.OLD_VERSION);
                }
            } else {
                protocollo.setReturnValue(ReturnValues.OLD_VERSION);
                // throw new DataException(
                // "Errore nell'aggiornamento del Protocollo.");
            }
        } catch (SQLException e) {
            logger.error("Aggiorna Protocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return protocollo;
    }

	

    /*
     * Metodo per il calcolo del progressivo "Numero Protocollo".
     * 
     * @see it.finsiel.siged.mvc.integration.ProtocolloDAO#getMaxNumProtocollo(java.sql.Connection,
     *      int, int)
     */
    public int getMaxNumProtocollo(Connection connection, int anno, int registro)
            throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("getMaxNumProtocollo() - Invalid Connection :" + connection);
                throw new DataException("Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(SELECT_ULTIMO_PROTOCOLLO);
            pstmt.setInt(1, registro);
            pstmt.setInt(2, anno);
            rs = pstmt.executeQuery();
            if (rs.next())
                return rs.getInt(1) + 1;
            else
            	return 1;
            	//TODO: migliorare la presa in carico del primo numero di protocollo
                //return getNextNumber();
        } catch (Exception e) {
            logger.error("getMaxNumProtocollo", e);
            throw new DataException("Cannot load getMaxNumProtocollo");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
    }

    private int getNextNumber() {
    	bundle = ResourceBundle.getBundle(SYSTEM_PARAMS);
    	String nextValue = bundle.getString("protocol_initial_number");
    	String path = bundle.getString("configuration_path");
    	int next = Integer.parseInt(nextValue);
    	if(next != 1) {
    		
    		File properties = new File(path); //TODO controllare bene il path per il file properties
    		if(properties.exists()) {
    			changeProperties(properties);
    		}
    	}
		return next;
	}

	private void changeProperties(File inFile) {
		 try {
		      
		      if (!inFile.isFile()) {
		        System.out.println("Parameter is not an existing file");
		        return;
		      }
		       
		      //Construct the new file that will later be renamed to the original filename.
		      File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
		      
		      BufferedReader br = new BufferedReader(new FileReader(inFile));
		      PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
		      
		      String line = null;

		      //Read from the original file and write to the new
		      //unless content matches data to be removed.
		      while ((line = br.readLine()) != null) {
		        
		        if (!line.trim().startsWith("protocol_initial_number")) {

		          pw.println(line);
		          pw.flush();
		        } else {
		        	pw.println("protocol_initial_number = 1");
			        pw.flush();
		        }
		      }
		      pw.close();
		      br.close();
		      
		      //Delete the original file
		      if (!inFile.delete()) {
		        System.out.println("Could not delete file");
		        return;
		      }
		      
		      //Rename the new file to the filename the original file had.
		      if (!tempFile.renameTo(inFile))
		        System.out.println("Could not rename file");
		      
		    }
		    catch (FileNotFoundException ex) {
		      ex.printStackTrace();
		    }
		    catch (IOException ex) {
		      ex.printStackTrace();
		    }
		  
		
	}

	public int getUltimoProtocollo(int anno, int registro) throws DataException {
        int ultimoProtocollo = 0;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_ULTIMO_PROTOCOLLO);
            pstmt.setInt(1, registro);
            pstmt.setInt(2, anno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ultimoProtocollo = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("Load Ultimo protocollo Registro", e);
            throw new DataException("Cannot load Ultimo protocollo Registro");
        } finally {
            jdbcMan.closeAll(rs, pstmt, connection);
        }
        return ultimoProtocollo;
    }

    /*
     * Legge un Protocollo dalla tabella usando la chiave
     * Anno/Registro/NumeroProtocollo.
     * 
     * @see it.finsiel.siged.mvc.integration.ProtocolloDAO#getProtocollo_By_Numero(int,
     *      int, int)
     */
    public ProtocolloVO getProtocolloByNumero(int anno, int registro,
            int numProtocollo) throws DataException {
        ProtocolloVO protocollo = new ProtocolloVO();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            protocollo = getProtocolloByNumero(connection, anno, registro,
                    numProtocollo);

        } catch (Exception e) {
            logger.error("Load Protocollo by ID", e);
            throw new DataException("Cannot load Protocollo by ID");
        } finally {
            jdbcMan.close(connection);
        }
        return protocollo;
    }

    public ProtocolloVO getProtocolloByNumero(Connection connection, int anno,
            int registro, int numProtocollo) throws DataException {
        ProtocolloVO protocollo = new ProtocolloVO();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger
                        .warn("getProtocolloByNumero (Connection connection, int anno, int registro,"
                                + "int numProtocollo) - Invalid Connection :"
                                + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(SELECT_PROTOCOLLO_BY_NUMERO);
            pstmt.setInt(1, anno);
            pstmt.setInt(2, registro);
            pstmt.setInt(3, numProtocollo);
            rs = pstmt.executeQuery();
            int id;
            if (rs.next()) {
                id = rs.getInt(1);
                protocollo = getProtocolloById(connection, id);
            }

        } catch (Exception e) {
            logger.error("Load Protocollo by ID", e);
            throw new DataException("Cannot load Protocollo by ID");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return protocollo;
    }

    public void aggiornaDocumentoPrincipaleId(Connection connection,
            int protocolloId, int documentoId) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger
                        .warn("aggiornaDocumentoPrincipaleId() - Invalid Connection :"
                                + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(UPDATE_DOCUMENTO_ID);
            pstmt.setInt(1, documentoId);
            pstmt.setInt(2, protocolloId);
            pstmt.executeUpdate(); 

            // logger.debug("Aggiornato documento_id:" + protocolloId + "," +
            // documentoId);

        } catch (Exception e) {
            logger.error("Aggiornamento documento_id", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void salvaAllegato(Connection connection,
            int protocollo_allegati_id, int protocolloId, int documentoId,
            int versione) throws DataException {
        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("salvaAllegato() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(INSERT_PROTOCOLLO_ALLEGATI);
            pstmt.setInt(1, protocollo_allegati_id);
            pstmt.setInt(2, protocolloId);
            pstmt.setInt(3, documentoId);
            pstmt.setInt(4, versione);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Save Allegato-Protocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    /*
     * Inserisce un insieme di AllacciVO nella tabella "protocollo_allacci"
     * 
     * @see it.finsiel.siged.mvc.integration.ProtocolloDAO#insertAllacci(java.sql.Connection,
     *      int, java.util.Collection)
     */

    public void salvaAllaccio(Connection connection, AllaccioVO allaccio)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("salvaAllaccio() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            if (allaccio != null) {
                pstmt = connection.prepareStatement(INSERT_PROTOCOLLO_ALLACCI);
                pstmt.setInt(1, allaccio.getId().intValue());
                pstmt.setInt(2, allaccio.getProtocolloId());
                pstmt.setInt(3, allaccio.isPrincipale() ? 1 : 0);
                pstmt.setInt(4, allaccio.getProtocolloAllacciatoId());
                pstmt.setInt(5, allaccio.getProtocolloId());
                pstmt.executeUpdate();
            }

            // logger.debug("Inserito Allaccio:" + allaccio);

        } catch (Exception e) {
            logger.error("Save Allaccio-Protocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    /*
     * TODO: Questo metodo deve essere modificato per permettere la
     * "paginazione" dei risultati. Restituisce un insieme di AllaccioView da
     * utilizzare nella pagina dei risultati a seguito di una ricerca che elenca
     * tutti i "protocolli allacciabili".
     * 
     * @see it.finsiel.siged.mvc.integration.ProtocolloDAO#getProtocolliAllacciabili(int,
     *      int, int)
     */

    public Collection getProtocolliAllacciabili(Utente utente,
            int numeroProtocolloDa, int numeroProtocolloA, int annoProtocollo,
            int protocolloId) throws DataException {
        ArrayList allacci = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strQuery = new StringBuffer(SELECT_ALLACCI);
        Organizzazione org = Organizzazione.getInstance();
        Ufficio uff = org.getUfficio(utente.getUfficioVOInUso().getId()
                .intValue());
        if (!uff.getValueObject().getTipo().equals(UfficioVO.UFFICIO_CENTRALE)) {
            String ufficiUtenti = uff.getListaUfficiDiscendentiId(utente);
            strQuery.append(" AND (EXISTS (SELECT * FROM ").append(
                    "protocollo_assegnatari ass ").append(
                    "WHERE ass.protocollo_id=p.protocollo_id ").append(
                    "AND ass.ufficio_assegnatario_id IN (")
                    .append(ufficiUtenti).append(")) OR").append(
                            " p.ufficio_protocollatore_id IN (").append(
                            ufficiUtenti).append(") OR").append(
                            " p.ufficio_mittente_id IN (").append(ufficiUtenti)
                    .append("))");
        }

        try {
            if (numeroProtocolloDa > 0) {
                strQuery.append(" AND NUME_PROTOCOLLO>=" + numeroProtocolloDa);
            }
            if (numeroProtocolloA > 0) {
                strQuery.append(" AND NUME_PROTOCOLLO<=" + numeroProtocolloA);
            }
            if (annoProtocollo > 0) {
                strQuery.append(" AND ANNO_REGISTRAZIONE=" + annoProtocollo);
            }
            strQuery
                    .append(" ORDER BY p.ANNO_REGISTRAZIONE DESC, p.NUME_PROTOCOLLO DESC");
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery.toString());

            pstmt.setInt(1, utente.getRegistroInUso());
            pstmt.setInt(2, protocolloId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                AllaccioView allaccio = new AllaccioView();
                allaccio.setProtAllacciatoId(rs.getInt("protocollo_id"));
                allaccio.setAnnoProtAllacciato(rs.getInt("ANNO_REGISTRAZIONE"));
                allaccio.setNumProtAllacciato(rs.getInt("NUME_PROTOCOLLO"));
                allaccio.setDataProtocollo(DateUtil.formattaData(rs.getDate(
                        "data_registrazione").getTime()));
                allaccio.setTipoProtocollo(rs.getString("FLAG_TIPO"));
                allaccio.setOggetto(rs.getString("TEXT_OGGETTO"));
                if (rs.getString("FLAG_TIPO_MITTENTE").equals("F")) {
                    allaccio.setMittente(rs.getString("DESC_COGNOME_MITTENTE")
                            + " "
                            + StringUtil.getStringa(rs
                                    .getString("DESC_NOME_MITTENTE")));
                } else {
                    allaccio.setMittente(rs
                            .getString("DESC_DENOMINAZIONE_MITTENTE"));
                }
                allacci.add(allaccio);
            }
        } catch (Exception e) {
            logger.error("getProtocolliAllacciabili", e);
            throw new DataException("Cannot load getProtocolliAllacciabili");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return allacci;

    }

    

    public Map getAllegatiProtocollo(int protocolloId) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map docs = new HashMap(2);
        Connection connection = null;
        DocumentoDelegate documentoDelegate = DocumentoDelegate.getInstance();
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(PROTOCOLLO_ALLEGATI);
            pstmt.setInt(1, protocolloId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // TODO: questa parte dovrebbe essere gestita dal
                // form.allegaDocumento, al momento vi � duplicazione della
                // logica di creazione della lista degli allegati.
                int id = rs.getInt("documento_id");
                DocumentoVO doc = documentoDelegate
                        .getDocumento(connection, id);
                ProtocolloBO.putAllegato(doc, docs);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException("Errore nella lettura dei documenti.");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return docs;
    }

    /*
     * Metodo che restituisce un insieme di AllaccioView da utilizzare nella
     * lista dei protocolli allacciati a quello in questione, ossia che possiede
     * id=protocolloId.
     * 
     * @see it.finsiel.siged.mvc.integration.ProtocolloDAO#getAllacci(int)
     */

    public Collection getAllacciProtocollo(int protocolloId)
            throws DataException {
        Collection allacci = new ArrayList();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            allacci = getAllacciProtocollo(connection, protocolloId);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " ProtocolloId :"
                    + protocolloId);
        } finally {
            jdbcMan.close(connection);
        }
        return allacci;
    }

    public Collection getAllacciProtocollo(Connection connection,
            int protocolloId) throws DataException {
        ArrayList allacci = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(PROTOCOLLO_ALLACCI);
            pstmt.setInt(1, protocolloId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                AllaccioVO allaccio = new AllaccioVO();
                allaccio.setId(rs.getInt("allaccio_id"));
                allaccio.setProtocolloId(rs.getInt("protocollo_id"));
                allaccio.setProtocolloAllacciatoId(rs
                        .getInt("protocollo_allacciato_id"));
                allaccio.setPrincipale(rs.getBoolean("flag_principale"));
                allaccio.setAllaccioDescrizione(rs.getInt("nume_protocollo")
                        + "/" + rs.getInt("anno_registrazione") + " ("
                        + rs.getString("flag_tipo") + ")");
                allacci.add(allaccio);
            }
        } catch (Exception e) {
            logger.error("getAllacci", e);
            throw new DataException("Cannot load getAllacci");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return allacci;
    }

    public AssegnatarioVO getAssegnatarioPerCompetenza(int protocolloId)
            throws DataException {
        AssegnatarioVO assegnatario = new AssegnatarioVO();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            assegnatario = getAssegnatarioPerCompetenza(connection,
                    protocolloId);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " ProtocolloId :"
                    + protocolloId);
        } finally {
            jdbcMan.close(connection);
        }
        return assegnatario;
    }

    public AssegnatarioVO getAssegnatarioPerCompetenza(Connection connection,
            int protocolloId) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AssegnatarioVO assegnatario = new AssegnatarioVO();
        try {
            pstmt = connection.prepareStatement(ASSEGNATARIO_PER_COMPETENZA);
            pstmt.setInt(1, protocolloId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                assegnatario.setId(rs.getInt("assegnatario_id"));
                assegnatario.setProtocolloId(rs.getInt("protocollo_id"));
                assegnatario.setDataAssegnazione(rs
                        .getDate("data_assegnazione"));
                assegnatario.setDataOperazione(rs.getDate("data_operazione"));
                assegnatario.setUfficioAssegnanteId(rs
                        .getInt("ufficio_assegnante_id"));
                assegnatario.setUfficioAssegnatarioId(rs
                        .getInt("ufficio_assegnatario_id"));
                assegnatario.setUtenteAssegnatarioId(rs
                        .getInt("utente_assegnatario_id"));
                assegnatario.setCompetente(rs.getBoolean("flag_competente"));
                assegnatario.setUtenteAssegnanteId(rs
                        .getInt("utente_assegnante_id"));
            }
        } catch (Exception e) {
            logger.error("getAssegnatarioPerCompetenzaProtocollo", e);
            throw new DataException(
                    "Cannot load getAssegnatarioPerCompetenzaProtocollo");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return assegnatario;
    }

    public Collection getAssegnatariProtocollo(int protocolloId)
            throws DataException {
        ArrayList assegnatari = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(PROTOCOLLO_ASSEGNATARI);
            pstmt.setInt(1, protocolloId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                AssegnatarioVO assegnatario = new AssegnatarioVO();
                assegnatario.setId(rs.getInt("assegnatario_id"));
                assegnatario.setProtocolloId(rs.getInt("protocollo_id"));
                assegnatario.setDataAssegnazione(rs
                        .getDate("data_assegnazione"));
                assegnatario.setDataOperazione(rs.getDate("data_operazione"));
                assegnatario.setUfficioAssegnanteId(rs
                        .getInt("ufficio_assegnante_id"));
                assegnatario.setUfficioAssegnatarioId(rs
                        .getInt("ufficio_assegnatario_id"));
                assegnatario.setUtenteAssegnatarioId(rs
                        .getInt("utente_assegnatario_id"));
                assegnatario.setCompetente(rs.getBoolean("flag_competente"));
                assegnatario.setUtenteAssegnanteId(rs
                        .getInt("utente_assegnante_id"));
                assegnatario.setStatoAssegnazione(rs.getString(
                        "stat_assegnazione").charAt(0));
                assegnatario.setMsgAssegnatarioCompetente(rs
                        .getString("messaggio"));
                assegnatari.add(assegnatario);
            }
        } catch (Exception e) {
            logger.error("getAssegnatariProtocollo", e);
            throw new DataException("Cannot load getAssegnatari");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return assegnatari;
    }

    public void salvaAssegnatario(Connection connection,
            AssegnatarioVO assegnatario, int versione) throws DataException {
        PreparedStatement pstmt = null;
        int recIns = 0;

        try {
            if (connection == null) {
                logger.warn("salvaAssegnatario() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            if (assegnatario != null) {
                pstmt = connection
                        .prepareStatement(INSERT_PROTOCOLLO_ASSEGNATARI);
                java.sql.Date now = new java.sql.Date((new java.util.Date())
                        .getTime());
                pstmt.setInt(1, assegnatario.getId().intValue());
                pstmt.setInt(2, assegnatario.getProtocolloId());
                pstmt.setDate(3, now);
                pstmt.setDate(4, now);
                pstmt.setInt(5, assegnatario.isCompetente() ? 1 : 0);
                pstmt.setInt(6, assegnatario.getUfficioAssegnatarioId());
                if (assegnatario.getUtenteAssegnatarioId() == 0) {
                    pstmt.setNull(7, Types.INTEGER);
                } else {
                    pstmt.setInt(7, assegnatario.getUtenteAssegnatarioId());
                }
                pstmt.setInt(8, assegnatario.getUfficioAssegnanteId());

                if (assegnatario.getUtenteAssegnanteId() == 0) {
                    pstmt.setNull(9, Types.INTEGER);
                } else {
                    pstmt.setInt(9, assegnatario.getUtenteAssegnanteId());
                }
                pstmt.setString(10, assegnatario.getStatoAssegnazione() + "");
                pstmt
                        .setString(11, assegnatario
                                .getMsgAssegnatarioCompetente());
                pstmt.setInt(12, versione);
                recIns = pstmt.executeUpdate();
            }
            // logger.debug("Numero Assegnatari-Protocollo inseriti:" + recIns);

        } catch (Exception e) {
            logger.error("Save Assegnatari-Protocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public int presaIncarico(Connection connection, ProtocolloVO protocolloVO,
            String tipoAzione, Utente utente) throws DataException {

        PreparedStatement pstmt = null;
        int fglagStatus = ReturnValues.SAVED;
        try {
            if (connection == null) {
                logger.warn("presaIncarico() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            /*
             * TODO: CONTROLLARE PRIMA LO STATO DEL PROTOCOLLO SE NON � STATO
             * GIA PRESO IN CARICO OPPURE SE LO STATO � TALE DA POTER ESSERE
             * GESTITO ALORA SI VA AVANTI ALTRIMENTI SI INVIA UN MESSAGGIO DI
             * ERRORE.
             */

            StringBuffer sqlUpdateAssegnatarioCompetenza = new StringBuffer(
                    "UPDATE PROTOCOLLO_ASSEGNATARI SET STAT_ASSEGNAZIONE=?");
            if ("R".equals(tipoAzione)) {
                sqlUpdateAssegnatarioCompetenza
                        .append(", utente_assegnatario_id=utente_assegnante_id, "
                                + "ufficio_assegnatario_id=ufficio_assegnante_id,"
                                + "ufficio_assegnante_id="
                                + utente.getUfficioInUso()
                                + ","
                                + "utente_assegnante_id="
                                + utente.getValueObject().getId().intValue());
            } else {
                sqlUpdateAssegnatarioCompetenza
                        .append(", utente_assegnatario_id=?");
            }
            sqlUpdateAssegnatarioCompetenza
                    .append(" WHERE protocollo_id=? AND flag_competente=1 AND versione=?");

            pstmt = connection.prepareStatement(sqlUpdateAssegnatarioCompetenza
                    .toString());
            // logger.debug(" presaIncarico: " + protocolloVO.getId());
            int indice = 1;
            pstmt.setString(indice++, tipoAzione);
            if (!"R".equals(tipoAzione)) {
                pstmt.setInt(indice++, utente.getValueObject().getId()
                        .intValue());
            }
            pstmt.setInt(indice++, protocolloVO.getId().intValue());
            pstmt.setInt(indice++, protocolloVO.getVersione() + 1);
            int n = pstmt.executeUpdate();
            if (n == 0) {
                fglagStatus = ReturnValues.OLD_VERSION;
                throw new DataException("Versione incongruente.");
            }
        } catch (Exception e) {
            logger.error("presaIncarico", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return fglagStatus;
    }

    

    public Map getProtocolliAssegnati(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA,
            String statoProtocollo, String statoScarico,
            String tipoUtenteUfficio) throws DataException {
        SortedMap protocolli = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = (Integer) o1;
                Integer i2 = (Integer) o2;
                return i2.intValue() - i1.intValue();
            }
        });

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String strQuery = "SELECT p.*, a.ufficio_assegnante_id, a.ufficio_assegnatario_id, "
                + " a.utente_assegnante_id, a.utente_assegnatario_id, a.messaggio "
                + " FROM PROTOCOLLI p, PROTOCOLLO_ASSEGNATARI a "
                + " WHERE registro_id=? AND stato_protocollo=? "
                + " AND p.protocollo_id =a.protocollo_id  AND";
        if (annoProtocolloDa > 0) {
            strQuery = strQuery + " ANNO_REGISTRAZIONE>=" + annoProtocolloDa
                    + " AND";
        }
        if (annoProtocolloA > 0) {
            strQuery = strQuery + " ANNO_REGISTRAZIONE<=" + annoProtocolloA
                    + " AND";
        }
        if (numeroProtocolloDa > 0) {
            strQuery = strQuery + " NUME_PROTOCOLLO>=" + numeroProtocolloDa
                    + " AND";
        }
        if (dataDa != null) {
            strQuery = strQuery + " data_registrazione >=? AND";
        }
        if (dataA != null) {
            strQuery = strQuery + " data_registrazione <=? AND";
        }
        if (numeroProtocolloA > 0) {
            strQuery = strQuery + " NUME_PROTOCOLLO<=" + numeroProtocolloA
                    + " AND";
        }
        if ("S".equals(statoScarico) && "T".equals(tipoUtenteUfficio)) {
            strQuery = strQuery
                    + " utente_assegnatario_id=? AND flag_competente=1 AND STAT_ASSEGNAZIONE=?";

        } else if (("S".equals(statoScarico) && "U".equals(tipoUtenteUfficio))
                || "A".equals(statoScarico) || "R".equals(statoScarico)) {
            strQuery = strQuery + " ufficio_assegnatario_id IN ("
                    + utente.getUfficiIds() + ") ";
            strQuery = strQuery + " AND utente_assegnatario_id IS NULL";
            strQuery = strQuery
                    + " AND flag_competente=1 AND STAT_ASSEGNAZIONE=?";

        } else if ("N".equals(statoScarico)) {
            strQuery = strQuery
                    + " utente_assegnatario_id=? AND flag_competente=1 AND STAT_ASSEGNAZIONE=?";
        }
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery);
            int currPstmt = 1;
            pstmt.setInt(currPstmt++, utente.getRegistroInUso());
            pstmt.setString(currPstmt++, statoScarico);
            if (dataDa != null) {
                pstmt.setDate(currPstmt++, new java.sql.Date(dataDa.getTime()));
            }
            if (dataA != null) {
                pstmt.setTimestamp(currPstmt++, new Timestamp(dataA.getTime()
                        + Constants.GIORNO_MILLISECONDS - 1));
            }
            if (("S".equals(statoScarico) && "T".equals(tipoUtenteUfficio))
                    || "N".equals(statoScarico)) {
                pstmt.setInt(currPstmt++, utente.getValueObject().getId()
                        .intValue());
            }
            pstmt.setString(currPstmt++, statoProtocollo);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                ReportProtocolloView protocollo = new ReportProtocolloView();
                protocollo.setProtocolloId(rs.getInt("protocollo_id"));
                protocollo.setNumeroProtocollo(rs.getInt("NUME_PROTOCOLLO"));
                protocollo.setAnnoProtocollo(rs.getInt("ANNO_REGISTRAZIONE"));
                if("M".equals(rs.getString("flag_tipo_mittente"))){
                    protocollo.setTipoMittente("M");
                }
                protocollo.setDataProtocollo(DateUtil.formattaData(rs.getDate(
                        "DATA_REGISTRAZIONE").getTime()));

                if (rs.getInt("FLAG_RISERVATO") == 1) {
                    protocollo.setOggetto(Parametri.PROTOCOLLO_RISERVATO);
                    protocollo.setMittente(Parametri.PROTOCOLLO_RISERVATO);

                } else {
                    protocollo.setOggetto(rs.getString("TEXT_OGGETTO"));
                    protocollo.setMittente(StringUtil.getStringa(rs
                            .getString("DESC_COGNOME_MITTENTE"))
                            + " "
                            + StringUtil.getStringa(rs
                                    .getString("DESC_NOME_MITTENTE"))
                            + StringUtil.getStringa(rs
                                    .getString("DESC_DENOMINAZIONE_MITTENTE")));
                }
                protocollo.setUfficio(rs.getString("ufficio_assegnante_id"));
                protocollo.setUtenteAssegnante(rs
                        .getString("utente_assegnante_id"));
                protocollo.setUfficioAssegnatario(rs
                        .getString("ufficio_assegnatario_id"));
                protocollo.setUtenteAssegnatario(rs
                        .getString("utente_assegnatario_id"));
                protocollo.setPdf(rs.getInt("DOCUMENTO_ID") > 0 ? true : false);
                protocollo.setDocumentoId(rs.getInt("DOCUMENTO_ID"));
                protocollo.setMessaggio(StringUtil.getStringa(rs
                        .getString("messaggio")));
                if (rs.getDate("data_scarico") != null)
                    protocollo.setDataScarico(DateUtil.formattaData(rs.getDate(
                            "data_scarico").getTime()));
                protocolli.put(new Integer(protocollo.getProtocolloId()),
                        protocollo);
            }
        } catch (Exception e) {
            logger.error("getProtocolliAssegnati", e);
            throw new DataException("Cannot load getProtocolliAssegnati");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return protocolli;

    }

    public Map getProtocolliRespinti(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA) throws DataException {
        SortedMap protocolli = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = (Integer) o1;
                Integer i2 = (Integer) o2;
                return i2.intValue() - i1.intValue();
            }
        });

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String strQuery = "SELECT P.*, ufficio_assegnatario_id, utente_assegnatario_id, "
                + " ufficio_assegnante_id, utente_assegnante_id, messaggio"
                + " FROM PROTOCOLLI P, PROTOCOLLO_ASSEGNATARI A"
                + " WHERE p.registro_id=? AND flag_competente=1 AND A.protocollo_id = P.protocollo_id "
                + " AND stat_assegnazione='R' "
                + " AND utente_assegnatario_id ="
                + utente.getValueObject().getId().intValue();
        if (annoProtocolloDa > 0) {
            strQuery = strQuery + " AND ANNO_REGISTRAZIONE>="
                    + annoProtocolloDa + " ";
        }
        if (annoProtocolloA > 0) {
            strQuery = strQuery + " AND ANNO_REGISTRAZIONE<=" + annoProtocolloA;
        }
        if (numeroProtocolloDa > 0) {
            strQuery = strQuery + " AND  NUME_PROTOCOLLO>="
                    + numeroProtocolloDa;
        }
        if (dataDa != null) {
            strQuery = strQuery + " AND  data_registrazione >=? ";
        }
        if (dataA != null) {
            strQuery = strQuery + "  AND data_registrazione <=? ";
        }
        if (numeroProtocolloA > 0) {
            strQuery = strQuery + " AND  NUME_PROTOCOLLO<=" + numeroProtocolloA;
        }

        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery);
            int currPstmt = 1;
            pstmt.setInt(currPstmt++, utente.getRegistroInUso());
            if (dataDa != null) {
                pstmt.setDate(currPstmt++, new java.sql.Date(dataDa.getTime()));
            }
            if (dataA != null) {
                pstmt.setTimestamp(currPstmt++, new Timestamp(dataA.getTime()
                        + Constants.GIORNO_MILLISECONDS - 1));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                ReportProtocolloView protocollo = new ReportProtocolloView();
                protocollo.setProtocolloId(rs.getInt("protocollo_id"));
                protocollo.setAnnoProtocollo(rs.getInt("ANNO_REGISTRAZIONE"));
                protocollo.setNumeroProtocollo(rs.getInt("NUME_PROTOCOLLO"));
                protocollo.setDataProtocollo(DateUtil.formattaData(rs.getDate(
                        "data_registrazione").getTime()));
                if (rs.getInt("FLAG_RISERVATO") == 1) {
                    protocollo.setOggetto(Parametri.PROTOCOLLO_RISERVATO);
                    protocollo.setMittente(Parametri.PROTOCOLLO_RISERVATO);

                } else {
                    protocollo.setOggetto(rs.getString("TEXT_OGGETTO"));
                    protocollo.setMittente(StringUtil.getStringa(rs
                            .getString("DESC_COGNOME_MITTENTE"))
                            + " "
                            + StringUtil.getStringa(rs
                                    .getString("DESC_NOME_MITTENTE"))
                            + StringUtil.getStringa(rs
                                    .getString("DESC_DENOMINAZIONE_MITTENTE")));
                }

                protocollo.setUfficioAssegnatario(""
                        + rs.getInt("ufficio_assegnante_id"));
                protocollo.setUtenteAssegnatario(""
                        + rs.getInt("utente_assegnante_id"));

                protocollo.setPdf(rs.getInt("DOCUMENTO_ID") > 0 ? true : false);
                protocollo.setDocumentoId(rs.getInt("DOCUMENTO_ID"));
                protocollo.setMessaggio(StringUtil.getStringa(rs
                        .getString("messaggio")));
                if("M".equals(rs.getString("flag_tipo_mittente"))){
                    protocollo.setTipoMittente("M");
                }
                protocolli.put(new Integer(protocollo.getProtocolloId()),
                        protocollo);
            }
        } catch (Exception e) {
            logger.error("getProtocolliRespinti", e);
            throw new DataException("Cannot load getProtocolliRespinti");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return protocolli;

    }

    

    public int updateScarico(Connection connection, ProtocolloVO protocolloVO,
            String flagScarico, Utente utente) throws DataException {
        PreparedStatement pstmt = null;
        int fglagStatus = ReturnValues.SAVED;
        Date toDay = new Date(System.currentTimeMillis());
        try {
            if (connection == null) {
                logger.warn("updateScarico() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            archiviaVersione(connection, protocolloVO.getId().intValue(),
                    protocolloVO.getVersione() + 1);
            pstmt = connection.prepareStatement(UPDATE_SCARICO);
            pstmt.setString(1, flagScarico);
            if ("R".equals(flagScarico) || "A".equals(flagScarico)) {
                pstmt.setDate(2, toDay);
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, utente.getValueObject().getUsername());
            pstmt.setInt(5, protocolloVO.getId().intValue());
            pstmt.setInt(6, protocolloVO.getVersione() + 1);
            int n = pstmt.executeUpdate();
            if (n == 0) {
                fglagStatus = ReturnValues.OLD_VERSION;
                throw new DataException("Versione incongruente.");
            } else {
                int indice = 1;
                String sqlUpdate = "UPDATE PROTOCOLLO_ASSEGNATARI set utente_assegnatario_id=?"
                        + " WHERE protocollo_id=? AND flag_competente=1";
                if ("R".equals(flagScarico) || "A".equals(flagScarico)) {
                    pstmt = connection.prepareStatement(sqlUpdate);
                    pstmt.setNull(indice++, Types.INTEGER);
                } else if ("N".equals(flagScarico)) {
                    pstmt = connection.prepareStatement(sqlUpdate);
                    pstmt.setInt(indice++, utente.getValueObject().getId()
                            .intValue());
                } else if ("F".equals(flagScarico)) {
                    sqlUpdate = "UPDATE PROTOCOLLO_ASSEGNATARI set"
                            + " ufficio_assegnatario_id=ufficio_assegnante_id,"
                            + " utente_assegnatario_id=utente_assegnante_id,"
                            + " stat_assegnazione='R',"
                            + " ufficio_assegnante_id="
                            + utente.getUfficioInUso() + ","
                            + " utente_assegnante_id="
                            + utente.getValueObject().getId().intValue()
                            + " WHERE protocollo_id=? AND flag_competente=1";

                    pstmt = connection.prepareStatement(sqlUpdate);
                }
                pstmt.setInt(indice++, protocolloVO.getId().intValue());
                pstmt.executeUpdate();

            }
        } catch (Exception e) {
            logger.error("updateScarico:" + e.getMessage(), e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return fglagStatus;
    }

    public SortedMap cercaProtocolli(Utente utente, Ufficio uff, HashMap sqlDB)
            throws DataException {
        SortedMap protocolli = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = (Integer) o1;
                Integer i2 = (Integer) o2;
                return i2.intValue() - i1.intValue();
            }
        });
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strQuery = new StringBuffer(SELECT_LISTA_PROTOCOLLI);
        if (!uff.getValueObject().getTipo().equals(UfficioVO.UFFICIO_CENTRALE)) {
            String ufficiUtenti = uff.getListaUfficiDiscendentiId(utente);
            strQuery.append(" AND (EXISTS (SELECT * FROM ").append(
                    "protocollo_assegnatari ass ").append(
                    "WHERE ass.protocollo_id=p.protocollo_id ").append(
                    "AND ass.ufficio_assegnatario_id IN (")
                    .append(ufficiUtenti).append(")) OR").append(
                            " p.ufficio_protocollatore_id IN (").append(
                            ufficiUtenti).append(") OR").append(
                            " p.ufficio_mittente_id IN (").append(ufficiUtenti)
                    .append("))");
        }
        if (sqlDB != null) {
            for (Iterator it = sqlDB.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                strQuery.append(" AND ").append(key.toString());
		        if(key.toString().contains(" IN ")) {
		        	List<Integer> documents_id = (List<Integer>)entry.getValue();
		        	strQuery.append(" (");
		        	for (int i = 0; i < documents_id.size(); i++) {
		        		strQuery.append("?");
		        		if(i < documents_id.size() -1) {
		        			strQuery.append(", ");
		        		}
					}
		        	strQuery.append(")");
		        }
            }
        }

        System.out.println("Query: " + strQuery.toString());
        int indiceQuery = 1;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery.toString());
            pstmt.setInt(indiceQuery++, utente.getRegistroInUso());

            if (sqlDB != null) {
                for (Iterator it = sqlDB.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Integer) {
                        pstmt.setInt(indiceQuery, ((Integer) value).intValue());
                    } else if (value instanceof String) {
                        if (key.toString().indexOf("LIKE") > 0) {
                            pstmt
                                    .setString(indiceQuery, value.toString()
                                            + "%");
                        } else {
                            pstmt.setString(indiceQuery, value.toString());
                        }
                    } else if (value instanceof java.util.Date) {
                        java.util.Date d = (java.util.Date) value;
                        pstmt.setTimestamp(indiceQuery, new java.sql.Timestamp(
                                d.getTime()));
                    } else if (value instanceof Boolean) {
                        pstmt.setBoolean(indiceQuery, ((Boolean) value)
                                .booleanValue());
                    } else if (value instanceof Long) {
                        pstmt.setLong(indiceQuery, ((Long) value).longValue());
                    } else if (value instanceof List) {
		            	 if (key.toString().indexOf(" IN ") > 0) {
		            		 for(int j = 0; j < ((List<Integer>)value).size(); j++) {
		            			 pstmt.setInt(indiceQuery, ((List<Integer>)value).get(j).intValue());
		            			 if(j < ((List<Integer>)value).size() -1) {
		            				 indiceQuery++;
		            			 }
		            		 }
		            	 }
		            }
                    indiceQuery++;
                }
            }

            rs = pstmt.executeQuery();
            ReportProtocolloView protocollo = null;
            StringBuffer dest_ass = null;
            while (rs.next()) {
                if (protocollo == null  || protocollo.getProtocolloId() != rs.getInt("protocollo_id")) {
                    if (protocollo != null) {
                        dest_ass.append("</ul>");
                        protocollo.setDestinatario(dest_ass.toString());
                    }
                    dest_ass = new StringBuffer("<ul>");
                    protocollo = new ReportProtocolloView();
                    protocollo.setProtocolloId(rs.getInt("protocollo_id"));
                    protocollo.setAnnoProtocollo(rs
                            .getInt("anno_registrazione"));
                    protocollo
                            .setNumeroProtocollo(rs.getInt("nume_protocollo"));
                    protocollo.setTipoProtocollo(rs.getString("flag_tipo"));

                    if (rs.getBoolean("flag_riservato")) {
                        protocollo.setOggetto(Parametri.PROTOCOLLO_RISERVATO);
                        protocollo.setMittente(Parametri.PROTOCOLLO_RISERVATO);
                        protocollo.setModificabile(false);
                    } else {
                        protocollo.setOggetto(rs.getString("text_oggetto"));
                        StringBuffer mittente = new StringBuffer();
                        if ("F".equals(rs.getString("flag_tipo_mittente"))) {
                            mittente.append(rs
                                    .getString("desc_cognome_mittente"));
                            if (rs.getString("desc_nome_mittente") != null) {
                                mittente.append(' ').append(
                                        rs.getString("desc_nome_mittente"));
                            }
                        } else if("M".equals(rs.getString("flag_tipo_mittente"))){
                            protocollo.setTipoMittente("M");
                            mittente.append(Parametri.MULTI_MITTENTE);
                        } else {
                            mittente.append(rs
                                    .getString("desc_denominazione_mittente"));
                        }
                        protocollo.setMittente(mittente.toString());
                        protocollo.setModificabile(true);
                    }
                    protocollo.setDataProtocollo(DateUtil.formattaData(rs
                            .getDate("data_registrazione").getTime()));

                    protocollo.setPdf(rs.getInt("documento_id") > 0);
                    protocollo.setDocumentoId(rs.getInt("documento_id"));
                    protocollo.setFilename(getDocId(rs.getInt("documento_id")));
                    protocollo.setTitolarioId(rs.getInt("titolario_id"));
                    protocollo.setStatoProtocollo(rs
                            .getString("stato_protocollo"));
                    protocolli.put(new Integer(protocollo.getProtocolloId()),
                            protocollo);

                }

                if (!rs.getBoolean("flag_riservato")) {
                    dest_ass.append("<li>");
                    if ("I".equals(protocollo.getTipoProtocollo())) {
                        if (rs.getBoolean("flag_competente")) {
                            dest_ass.append("<em>");
                        }
                        dest_ass.append(rs.getString("ufficio_assegnatario"));
                        if (rs.getString("cognome_assegnatario") != null) {
                            dest_ass.append(" / ").append(
                                    rs.getString("cognome_assegnatario"));
                            if (rs.getString("nome_assegnatario") != null) {
                                dest_ass.append(' ').append(
                                        rs.getString("nome_assegnatario"));
                            }
                        }
                        if (rs.getBoolean("flag_competente")) {
                            dest_ass.append("</em>");
                        }
                    } else {
                        if (rs.getInt("flag_conoscenza") == 0) {
                            dest_ass.append("<em>");
                        }
                        dest_ass.append(rs.getString("destinatario"));
                        if (rs.getInt("flag_conoscenza") == 0) {
                            dest_ass.append("</em>");
                        }
                    }
                    dest_ass.append("</li>");
                } else {
                    dest_ass = new StringBuffer(Parametri.PROTOCOLLO_RISERVATO);
                }

            }
            if (protocollo != null) {
                dest_ass.append("</ul>");
                protocollo.setDestinatario(dest_ass.toString());
            }

        } catch (Exception e) {
            logger.error("cercaProtocolli", e);
            throw new DataException("Cannot load cercaProtocolli");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return protocolli;

    }

    

    public Collection getMittenti(String mittente) throws DataException {
        ArrayList mittenti = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(MITTENTI_PROTOCOLLI);
            pstmt.setString(1, mittente.toLowerCase() + "%");
            pstmt.setString(2, mittente.toLowerCase() + "%");

            rs = pstmt.executeQuery();
            while (rs.next()) {
                MittenteView mittenteView = new MittenteView();
                mittenteView.setTipo(rs.getString("FLAG_TIPO_MITTENTE"));
                mittenteView.setCognome(rs.getString("DESC_COGNOME_MITTENTE"));
                mittenteView.setNome(StringUtil.getStringa(rs
                        .getString("DESC_NOME_MITTENTE")));
                mittenteView.setDenominazione(rs
                        .getString("DESC_DENOMINAZIONE_MITTENTE"));
                mittenti.add(mittenteView);
            }
        } catch (Exception e) {
            logger.error("getMittenti", e);
            throw new DataException("Cannot load getMittenti");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return mittenti;
    }

    public Map getDestinatariProtocollo(int protocolloId) throws DataException {
        HashMap destinatari = new HashMap();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(PROTOCOLLO_DESTINATARI);
            pstmt.setInt(1, protocolloId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                DestinatarioVO destinatarioVO = new DestinatarioVO();
                destinatarioVO.setId(rs.getInt("destinatario_id"));
                destinatarioVO.setFlagTipoDestinatario(rs
                        .getString("flag_tipo_destinatario"));
                destinatarioVO.setIndirizzo(rs.getString("indirizzo"));
                destinatarioVO.setEmail(rs.getString("email"));
                destinatarioVO.setDestinatario(rs.getString("DESTINATARIO"));
                destinatarioVO.setTitoloId(rs.getInt("titolo_id"));
                destinatarioVO.setCitta(rs.getString("citta"));
                destinatarioVO.setNote(rs.getString("note"));
                destinatarioVO.setDataSpedizione(rs.getDate("data_spedizione"));
                destinatarioVO.setFlagConoscenza("1".equals(rs
                        .getString("flag_conoscenza")));
                destinatarioVO.setProtocolloId(rs.getInt("protocollo_id"));
                destinatarioVO.setDataEffettivaSpedizione(rs
                        .getDate("data_effettiva_spedizione"));
                destinatarioVO.setVersione(rs.getInt("versione"));
                destinatarioVO.setMezzoSpedizioneId(rs
                        .getInt("mezzo_spedizione_id"));
                destinatarioVO.setMezzoDesc(rs.getString("desc_spedizione"));
                destinatarioVO.setCodicePostale(rs.getString("codice_postale"));
                destinatari.put(String.valueOf(destinatarioVO.getId()),
                        destinatarioVO);
            }
        } catch (Exception e) {
            logger.error("getDestinatariProtocollo", e);
            throw new DataException("Cannot load getDestinatariProtocollo");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return destinatari;
    }

    public Collection getDestinatari(String destinatario) throws DataException {
        ArrayList destinatari = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(DESTINATARI_PROTOCOLLI);
            pstmt.setString(1, destinatario + "%");

            rs = pstmt.executeQuery();
            while (rs.next()) {
                DestinatarioVO destinatarioVO = new DestinatarioVO();
                destinatarioVO.setDestinatario(rs.getString("DESTINATARIO"));
                destinatari.add(destinatarioVO);
            }
        } catch (Exception e) {
            logger.error("getDestinatari", e);
            throw new DataException("Cannot load getDestinatari");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return destinatari;
    }

    public int annullaProtocollo(Connection connection,
            ProtocolloVO protocolloVO, Utente utente) throws DataException {
        PreparedStatement pstmt = null;
        int fglagStatus = ReturnValues.SAVED;
        try {
            Date toDay = new Date(System.currentTimeMillis());
            if (connection == null) {
                logger.warn("annullaProtocollo() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            archiviaVersione(connection, protocolloVO.getId().intValue(),
                    protocolloVO.getVersione() + 1);
            pstmt = connection.prepareStatement(ANNULLA_PROTOCOLLO);
            pstmt.setString(1, protocolloVO.getStatoProtocollo());
            pstmt.setDate(2, toDay);
            pstmt.setString(3, protocolloVO.getNotaAnnullamento());
            pstmt.setString(4, protocolloVO.getProvvedimentoAnnullamento());
            pstmt.setInt(5, protocolloVO.getId().intValue());
            pstmt.setInt(6, protocolloVO.getVersione() + 1);
            int n = pstmt.executeUpdate();
            if (n == 0) {
                fglagStatus = ReturnValues.OLD_VERSION;
                throw new DataException("Versione incongruente.");
            }
        } catch (Exception e) {
            logger.error("annullaProtocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return fglagStatus;
    }

    public int salvaSegnatura(Connection connection, SegnaturaVO segnaturaVO)
            throws DataException {
        PreparedStatement pstmt = null;
        int fglagStatus = ReturnValues.SAVED;
        try {
            if (connection == null) {
                logger.warn("salvaSignature() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(INSERT_SEGNATURE);
            pstmt.setInt(1, segnaturaVO.getId().intValue());
            pstmt.setInt(2, segnaturaVO.getFkProtocolloId());
            pstmt.setString(3, segnaturaVO.getTipoProtocollo());
            pstmt.setString(4, segnaturaVO.getTextSegnatura());
            pstmt.setString(5, segnaturaVO.getRowCreatedUser());
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("salvaSignature", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return fglagStatus;
    }

    public void eliminaDestinatariProtocollo(Connection connection,
            int protocolloId) throws DataException {
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(DELETE_DESTINATARI);
            pstmt.setInt(1, protocolloId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("eliminaAllacciProtocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void salvaDestinatario(Connection connection,
            DestinatarioVO destinatario, int versione) throws DataException {
        PreparedStatement pstmt = null;
        int recIns = 0;

        try {
            if (connection == null) {
                logger.warn("salvaDestinatario() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            if (destinatario != null) {
                pstmt = connection
                        .prepareStatement(INSERT_PROTOCOLLO_DESTINATARI);
                java.sql.Date now = new java.sql.Date((new java.util.Date())
                        .getTime());
                pstmt.setInt(1, destinatario.getId().intValue());
                pstmt.setString(2, destinatario.getFlagTipoDestinatario());
                pstmt.setString(3, destinatario.getIndirizzo());
                pstmt.setString(4, destinatario.getEmail());
                pstmt.setString(5, destinatario.getDestinatario());
                pstmt.setString(6, destinatario.getCitta());
                if (destinatario.getDataSpedizione() != null) {
                    pstmt.setDate(7, new java.sql.Date(destinatario
                            .getDataSpedizione().getTime()));
                } else {
                    pstmt.setNull(7, Types.DATE);
                }
                pstmt
                        .setString(8, destinatario.getFlagConoscenza() ? "1"
                                : "0");
                pstmt.setInt(9, destinatario.getProtocolloId());
                pstmt.setDate(10, now);
                pstmt.setInt(11, versione);

                if (destinatario.getMezzoSpedizioneId() != 0)
                    pstmt.setInt(12, destinatario.getMezzoSpedizioneId());
                else
                    pstmt.setNull(12, Types.INTEGER);

                // if (destinatario.getMezzoSpedizioneId() != 0)
                // pstmt.setInt(12, destinatario.getMezzoSpedizioneId());
                // else
                // pstmt.setNull(12, Types.INTEGER);

                if (destinatario.getTitoloId() == 0) {
                    destinatario.setTitoloId(999);
                }
                pstmt.setInt(13, destinatario.getTitoloId());

                // Modifica greco
                pstmt.setString(14, destinatario.getMezzoDesc());
                // ************

                pstmt.setString(15, destinatario.getNote());

                pstmt.setString(16, destinatario.getCodicePostale());
                recIns = pstmt.executeUpdate();
                // salvaDestinatarioMezzi(connection, destinatario, versione);

            }
            // logger.debug("Destinatari-Protocollo inseriti:" + recIns);

        } catch (Exception e) {
            logger.error("Save Destinatari-Protocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public Collection getProtocolliByProtMittente(Utente utente,
            String protMittente) throws DataException {
        ArrayList protocolli = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String protAllacciabili = "SELECT protocollo_id, ANNO_REGISTRAZIONE, NUME_PROTOCOLLO,"
                    + " data_registrazione, flag_tipo_mittente, desc_denominazione_mittente, desc_cognome_mittente,"
                    + " desc_nome_mittente, FLAG_TIPO, text_oggetto FROM PROTOCOLLI WHERE"
                    + " registro_id=? AND nume_protocollo_mittente =? AND aoo_id=?"
                    + "	ORDER BY ANNO_REGISTRAZIONE desc, NUME_PROTOCOLLO desc";
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(protAllacciabili);
            pstmt.setInt(1, utente.getRegistroInUso());
            pstmt.setString(2, protMittente);
            pstmt.setInt(3, utente.getAreaOrganizzativa().getId().intValue());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ReportProtocolloView protocolloView = new ReportProtocolloView();
                protocolloView.setAnnoProtocollo(rs
                        .getInt("ANNO_REGISTRAZIONE"));
                protocolloView
                        .setNumeroProtocollo(rs.getInt("NUME_PROTOCOLLO"));
                protocolloView.setDataProtocollo(DateUtil.formattaData(rs
                        .getDate("data_registrazione").getTime()));
                protocolloView.setTipoProtocollo(rs.getString("FLAG_TIPO"));
                protocolloView.setOggetto(rs.getString("text_oggetto"));
                if (rs.getString("flag_tipo_mittente").equals("F")) {
                    protocolloView.setMittente(rs
                            .getString("desc_cognome_mittente")
                            + " "
                            + StringUtil.getStringa(rs
                                    .getString("desc_nome_mittente")));
                } else {
                    protocolloView.setMittente(rs
                            .getString("DESC_DENOMINAZIONE_MITTENTE"));
                }
                protocolli.add(protocolloView);
            }
        } catch (Exception e) {
            logger.error("getProtocolliByProtMittente", e);
            throw new DataException("Cannot load getProtocolliByProtMittente");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return protocolli;
    }

    public Collection getMezziSpedizione(Connection connection, String idMezzi)
            throws DataException {
        Collection mezzi = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String queryMezzi = "SELECT * FROM SPEDIZIONI WHERE spedizioni_id=?";
            pstmt = connection.prepareStatement(queryMezzi.toString());
            pstmt.setString(1, idMezzi);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                SpedizioneDestinatarioVO sp = new SpedizioneDestinatarioVO();
                sp.setSpedizioneId(rs.getInt("spedizioni_id"));
                sp.setSpedizioneDesc(rs.getString("desc_spedizione"));
                mezzi.add(sp);
            }
        } catch (Exception e) {
            logger.error("getProtocolliToExport", e);
            throw new DataException("Cannot load getProtocolliToExport");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return mezzi;
    }

    // todo: Destinatari mezzo spedizione
    public Map getMezziSpedizioneByProteDest(int protocolloId,
            int destinatarioId) throws DataException {
        HashMap mezzi = new HashMap();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String queryMezzi = "select pd.*,s.desc_spedizione from protocollo_destinatari_mspediz pd, spedizioni s where pd.protocollo_id=? and pd.destinatario_id=? and pd.spedizioni_id=s.spedizioni_id";
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(queryMezzi);
            pstmt.setInt(1, protocolloId);
            pstmt.setInt(2, destinatarioId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                SpedizioneDestinatarioVO sp = new SpedizioneDestinatarioVO();
                sp.setSpedizioneId(rs.getInt("spedizioni_id"));
                sp.setDestinatarioId(rs.getInt("destinatario_id"));
                sp.setProtocolloId(rs.getInt("protocollo_id"));
                sp.setSpedizioneDesc(rs.getString("desc_spedizione"));
                sp.setVersione(rs.getInt("versione"));
                mezzi.put(String.valueOf(sp.getSpedizioneId()), sp);
            }
        } catch (Exception e) {
            logger.error("getMezziSpedizione", e);
            throw new DataException("Cannot load getMezziSpedizione");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return mezzi;
    }

    // TODO: link Doc
    public String getDocId(int documentoId) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String desc = new String();
        try {
            String queryDocId = "SELECT * FROM DOCUMENTI where documento_id=?";
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(queryDocId);
            pstmt.setInt(1, documentoId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                desc = rs.getString(3);
            }
        } catch (Exception e) {
            logger.error("getMezziSpedizione", e);
            throw new DataException("Cannot load getMezziSpedizione");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return desc;
    }

    public boolean esisteAllaccio(Connection connection, int protocolloId,
            int protocolloAllacciatoId) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean trovato = false;
        final String SELECT_ALLACCIO = "select count(protocollo_id) FROM protocollo_allacci "
                + "WHERE protocollo_id = ? and protocollo_allacciato_id = ?";

        try {
            pstmt = connection.prepareStatement(SELECT_ALLACCIO);
            pstmt.setInt(1, protocolloId);
            pstmt.setInt(2, protocolloAllacciatoId);
            rs = pstmt.executeQuery();
            rs.next();
            trovato = (rs.getInt(1) > 0);

        } catch (Exception e) {
            logger.error("esisteAllaccio", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return trovato;
    }

    public void eliminaAllacciProtocollo(Connection connection, int protocolloId)
            throws DataException {
        PreparedStatement pstmt = null;

        final String DELETE_ALLACCI = "DELETE FROM protocollo_allacci WHERE protocollo_id = ?";

        try {
            pstmt = connection.prepareStatement(DELETE_ALLACCI);
            pstmt.setInt(1, protocolloId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("eliminaAllacciProtocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void eliminaAllaccioProtocollo(Connection connection,
            int protocolloId, int protocolloAllacciatoId) throws DataException {
        PreparedStatement pstmt = null;

        final String DELETE_ALLACCIO = "DELETE FROM protocollo_allacci WHERE "
                + " protocollo_id = ?  AND protocollo_allacciato_id=? OR "
                + " protocollo_id = ?  AND protocollo_allacciato_id=? ";

        try {
            pstmt = connection.prepareStatement(DELETE_ALLACCIO);
            pstmt.setInt(1, protocolloId);
            pstmt.setInt(2, protocolloAllacciatoId);
            pstmt.setInt(3, protocolloAllacciatoId);
            pstmt.setInt(4, protocolloId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("eliminaAllaccioProtocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void eliminaAssegnatariProtocollo(Connection connection,
            int protocolloId) throws DataException {
        PreparedStatement pstmt = null;

        final String DELETE_ASSEGNATARI = "DELETE FROM protocollo_assegnatari WHERE protocollo_id = ?";

        try {
            pstmt = connection.prepareStatement(DELETE_ASSEGNATARI);
            pstmt.setInt(1, protocolloId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("eliminaAllacciProtocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public int riassegnaProtocollo(ProtocolloIngresso protocollo, Utente utente)
            throws DataException {
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            Collection assegnatari = protocollo.getAssegnatari();
            int protocolloId = protocollo.getProtocollo().getId().intValue();
            int versione = protocollo.getProtocollo().getVersione() + 1;
            if (assegnatari != null) {
                // statusFlag = updateScarico(connection, protocollo
                // .getProtocollo(), protocollo.getProtocollo()
                // .getStatoProtocollo(), utente);
                aggiornaProtocollo(connection, protocollo.getProtocollo());
                eliminaAssegnatariProtocollo(connection, protocolloId);
                for (Iterator i = assegnatari.iterator(); i.hasNext();) {
                    AssegnatarioVO assegnatario = (AssegnatarioVO) i.next();
                    assegnatario.setProtocolloId(protocolloId);
                    assegnatario.setId(IdentificativiDelegate.getInstance()
                            .getNextId(connection,
                                    NomiTabelle.PROTOCOLLO_ASSEGNATARI));

                    // TODO AGGIORNARE LA VERSIONE
                    salvaAssegnatario(connection, assegnatario, versione);
                }
                // aggiorna stato protocollo
                // protocollo.getProtocollo().setDataScarico(null);
            }
            connection.commit();
            statusFlag = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("failed riassegnaProtocollo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int getDocumentoDefault(int aooId) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int documentoDefaultId = 0;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_DOCUMENTO_DEFAULT);
            pstmt.setInt(1, aooId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                documentoDefaultId = rs.getInt("tipo_documento_id");
            }
        } catch (Exception e) {
            logger.error("Load getDocumentoDefault", e);
            throw new DataException("Cannot load getDocumentoDefault");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return documentoDefaultId;
    }

    // gestione registro d'emergenza
    public Collection getProtocolliToExport(Connection connection,
            int registroId) throws DataException {
        Collection protocolli = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String strQuery = "SELECT * FROM PROTOCOLLI WHERE registro_id=? and (num_prot_emergenza=0 "
                    + "OR num_prot_emergenza IS NULL) ORDER BY PROTOCOLLO_ID DESC";
            pstmt = connection.prepareStatement(strQuery.toString());
            pstmt.setInt(1, registroId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ProtocolloVO protocollo = new ProtocolloVO();
                protocollo.setId(rs.getInt("protocollo_id"));
                protocollo
                        .setAnnoRegistrazione(rs.getInt("ANNO_REGISTRAZIONE"));
                protocollo.setNumProtocollo(rs.getInt("NUME_PROTOCOLLO"));
                protocollo.setOggetto(rs.getString("TEXT_OGGETTO"));
                protocollo.setFlagTipo(rs.getString("FLAG_TIPO"));
                protocollo.setDataRegistrazione(rs
                        .getDate("data_registrazione"));
                protocolli.add(protocollo);
            }
        } catch (Exception e) {
            logger.error("getProtocolliToExport", e);
            throw new DataException("Cannot load getProtocolliToExport");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return protocolli;
    }

    public void updateRegistroEmergenza(Connection connection)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("updateRegistroEmergenza() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection
                    .prepareStatement("UPDATE PROTOCOLLI SET num_prot_emergenza = nume_protocollo WHERE num_prot_emergenza=0 OR num_prot_emergenza IS NULL");
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("updateScarico", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void updateMsgAssegnatarioCompetenteByIdProtocollo(
            Connection connection, String msgAssegnatarioCompetente,
            int protocolloId) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("updateMsgAssegnatarioCompetenteByIdProtocollo() - Invalid Connection :"
                                + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            if (msgAssegnatarioCompetente != null && protocolloId > 0) {
                pstmt = connection.prepareStatement(UPDATE_MSG_ASSEGNATARIO_COMPETENTE);
                pstmt.setString(1, msgAssegnatarioCompetente);
                pstmt.setInt(2, protocolloId);
                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            logger.error("Error: updateMsgAssegnatarioCompetenteByIdProtocollo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public Collection getProcedimentiProtocollo(int protocolloId)
            throws DataException {
        ArrayList procedimenti = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();

            pstmt = connection.prepareStatement(PROTOCOLLO_PROCEDIMENTI);
            pstmt.setInt(1, protocolloId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ProtocolloProcedimentoVO procedimento = new ProtocolloProcedimentoVO();
                procedimento.setProtocolloId(protocolloId);
                procedimento.setProcedimentoId(rs.getInt("procedimento_id"));
                procedimento.setNumeroProcedimento(rs.getString("anno")
                        + StringUtil.formattaNumeroProcedimento(rs
                                .getString("numero"), 7));
                procedimento.setOggetto(rs.getString("oggetto"));
                procedimento.setModificabile(true);
                procedimenti.add(procedimento);
            }

            jdbcMan.close(rs);
            jdbcMan.close(pstmt);

            pstmt = connection.prepareStatement(PROCEDIMENTI_PROTOCOLLO);
            pstmt.setInt(1, protocolloId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                ProtocolloProcedimentoVO procedimento = new ProtocolloProcedimentoVO();
                procedimento.setProtocolloId(protocolloId);
                procedimento.setProcedimentoId(rs.getInt("procedimento_id"));
                procedimento.setNumeroProcedimento(rs.getString("anno")
                        + StringUtil.formattaNumeroProcedimento(rs
                                .getString("numero"), 7));
                procedimento.setOggetto(rs.getString("oggetto"));
                procedimento.setModificabile(false);
                procedimenti.add(procedimento);
            }

        } catch (Exception e) {
            logger.error("getProcedimentiProtocollo", e);
            throw new DataException("Cannot load procedimenti");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return procedimenti;
    }

    public void inserisciProcedimenti(Connection connection, ArrayList ids)
            throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            for (Iterator it = ids.iterator(); it.hasNext();) {
                ProtocolloProcedimentoVO ppVO = (ProtocolloProcedimentoVO) it.next();
                pstmt = connection.prepareStatement(INSERT_PROTOCOLLI_PROCEDIMENTI);
                pstmt.setInt(1, ppVO.getProcedimentoId());
                pstmt.setInt(2, ppVO.getProtocolloId());
                pstmt.setDate(3, new Date(ppVO.getRowCreatedTime().getTime()));
                pstmt.setString(4, ppVO.getRowCreatedUser());
                pstmt.setString(5, ppVO.getRowUpdatedUser());
                pstmt.setDate(6, new Date(ppVO.getRowUpdatedTime().getTime()));
                pstmt.setInt(7, ppVO.getVersione());
                pstmt.executeUpdate();
                jdbcMan.close(pstmt);
            }
        } catch (Exception e) {
            logger.error("inserisciProcedimenti()", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void inserisciProcedimenti(Connection connection,
            ProtocolloProcedimentoVO protocolloProcedimentiVO)
            throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException("Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(INSERT_PROTOCOLLI_PROCEDIMENTI);
            pstmt.setInt(1, protocolloProcedimentiVO.getProcedimentoId());
            pstmt.setInt(2, protocolloProcedimentiVO.getProtocolloId());
            if (protocolloProcedimentiVO.getRowCreatedTime() != null) {
                pstmt.setDate(3, new Date(protocolloProcedimentiVO
                        .getRowCreatedTime().getTime()));
            } else {
                pstmt.setDate(3, null);
            }
            if (protocolloProcedimentiVO.getRowUpdatedTime() != null) {
                pstmt.setDate(6, new Date(protocolloProcedimentiVO
                        .getRowUpdatedTime().getTime()));
            } else {
                pstmt.setDate(6, null);
            }

            pstmt.setString(4, protocolloProcedimentiVO.getRowCreatedUser());
            pstmt.setString(5, protocolloProcedimentiVO.getRowUpdatedUser());

            pstmt.setInt(7, protocolloProcedimentiVO.getVersione());
            pstmt.executeUpdate();
            jdbcMan.close(pstmt);

        } catch (Exception e) {
            logger.error("inserisciProcedimenti()", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void cancellaProcedimenti(Connection connection, int protocolloId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(DELETE_PROCEDIMENTI_PROTOCOLLO);
            pstmt.setInt(1, protocolloId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("ProtocolloDAOjdbc-cancellaProcedimenti", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public boolean isUtenteAbilitatoView(Utente utente, Ufficio uff,
            int protocolloId) throws DataException {

        boolean abilitato = false;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = jdbcMan.getConnection();
            StringBuffer strQuery = new StringBuffer();
            strQuery
                    .append("SELECT count(protocollo_id) FROM protocolli p WHERE aoo_id = ?");
            strQuery.append(" AND protocollo_id=").append(protocolloId);
            if (!uff.getValueObject().getTipo().equals(
                    UfficioVO.UFFICIO_CENTRALE)) {
                String ufficiUtenti = uff.getListaUfficiDiscendentiId(utente);
                strQuery.append(" AND (EXISTS (SELECT * FROM ").append(
                        "protocollo_assegnatari ass ").append(
                        "WHERE ass.protocollo_id=p.protocollo_id ").append(
                        "AND ass.ufficio_assegnatario_id IN (").append(
                        ufficiUtenti).append(")) OR").append(
                        " p.ufficio_protocollatore_id IN (").append(
                        ufficiUtenti).append(") OR").append(
                        " p.ufficio_mittente_id IN (").append(ufficiUtenti)
                        .append("))");
            }

            pstmt = connection.prepareStatement(strQuery.toString());
            pstmt.setInt(1, utente.getAreaOrganizzativa().getId().intValue());
            // logger.debug(strQuery);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                abilitato = (rs.getInt(1) > 0);
            }

        } catch (Exception e) {
            logger.error("isUtenteAbilitatoView", e);
            throw new DataException("Cannot load isUtenteAbilitatoView");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return abilitato;

    }

    private static final String INSERT_PROTOCOLLI_PROCEDIMENTI = "insert into protocollo_procedimenti ("
            + "procedimento_id, protocollo_id, row_created_time, row_created_user, "
            + "row_updated_user, row_updated_time, versione) values (?,?,?,?,?,?,?)";

    private static final String DELETE_PROCEDIMENTI_PROTOCOLLO = "delete from protocollo_procedimenti where protocollo_id=?";

    public final static String SELECT_DOCUMENTO_DEFAULT = "SELECT tipo_documento_id FROM tipi_documento WHERE flag_default='1' AND aoo_id=?";

    // public final static String SELECT_ULTIMO_PROTOCOLLO = "SELECT
    // nume_protocollo, data_documento FROM protocolli "
    // + "WHERE protocollo_id = (SELECT MAX(protocollo_id) FROM protocolli WHERE
    // anno_registrazione = ? AND registro_id = ?)";

    public final static String SELECT_ULTIMO_PROTOCOLLO = "SELECT MAX(nume_protocollo) FROM protocolli WHERE registro_id = ? AND anno_registrazione = ?";

    // Destinatari protocollo in uscita
    private final static String INSERT_PROTOCOLLO_DESTINATARI = "INSERT INTO protocollo_destinatari"
            + " (destinatario_id, flag_tipo_destinatario, indirizzo, email, destinatario, "
            + " citta, data_spedizione, flag_conoscenza, protocollo_id, data_effettiva_spedizione,versione, mezzo_spedizione_id"
            + ", titolo_id, mezzo_spedizione,note,codice_postale "
            + ")"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
            + ", ?"
            + ", ?,?,?)";

    // salva SEGNATURE
    public final static String INSERT_SEGNATURE = "INSERT INTO segnature"
            + " (segnature_id, protocollo_id, tipo_protocollo, text_segnatura, row_created_user) VALUES (?, ?, ?, ?, ?)";

    // Annullamento protocollo
    public final static String ANNULLA_PROTOCOLLO = "UPDATE PROTOCOLLI SET stato_protocollo=?, data_annullamento=?, "
            + "text_nota_annullamento=?, text_provvedimento_annullament=?"
            + " WHERE protocollo_id=? AND versione=?";

    // Selezione dei destinatari di un protocollo in uscita
    public final static String PROTOCOLLO_DESTINATARI = "SELECT PD.*, S.DESC_SPEDIZIONE FROM PROTOCOLLO_DESTINATARI PD left outer join spedizioni s on (PD.mezzo_spedizione_id=s.spedizioni_id)"
            + " WHERE pd.protocollo_id=?  ORDER BY DESTINATARIO";

    // Selezione dei destinatari dei protocolli per destianatario
    public final static String DESTINATARI_PROTOCOLLI = "SELECT DISTINCT DESTINATARIO FROM PROTOCOLLO_DESTINATARI"
            + " WHERE DESTINATARIO LIKE ?  ORDER BY DESTINATARIO";

    // Selezione dei mittenti inputati nei protocolli per nome mittente
    public final static String MITTENTI_PROTOCOLLI = "SELECT DISTINCT flag_tipo_mittente,desc_denominazione_mittente,"
            + "desc_cognome_mittente, desc_nome_mittente FROM PROTOCOLLI "
            + " WHERE lower(desc_denominazione_mittente) LIKE ? OR lower(desc_cognome_mittente) LIKE ?"
            + " ORDER BY desc_denominazione_mittente,desc_cognome_mittente ";

    public final static String SELECT_MAX_NUM_PROTOCOLLO = "SELECT MAX(NUME_PROTOCOLLO) FROM PROTOCOLLI "
            + "WHERE ANNO_REGISTRAZIONE=? AND registro_id=?)";

    public final static String INSERT_PROTOCOLLO = "INSERT INTO protocolli ("
            + " protocollo_id, anno_registrazione, nume_protocollo, data_registrazione,"
            + " flag_tipo_mittente, text_oggetto, flag_tipo, data_documento,"
            + " tipo_documento_id, aoo_id, registro_id, utente_protocollatore_id,"
            + " ufficio_protocollatore_id, desc_denominazione_mittente,"
            + " desc_cognome_mittente, desc_nome_mittente, indi_mittente, indi_cap_mittente,"
            + " indi_localita_mittente, indi_provincia_mittente, annotazione_chiave,"
            + " annotazione_posizione, annotazione_descrizione, titolario_id, row_created_user,"
            + " flag_riservato, ufficio_mittente_id, utente_mittente_id,"
            + " nume_protocollo_mittente, data_ricezione, documento_id,"
            + " stato_protocollo, flag_mozione,num_prot_emergenza,data_annullamento, text_provvedimento_annullament,"
            + " text_nota_annullamento,data_effettiva_registrazione, row_created_time, versione, registro_anno_numero) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
            + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public final static String INSERT_STORIA_PROTOCOLLO = "INSERT INTO storia_protocolli ("
            + " protocollo_id, anno_registrazione, nume_protocollo, data_registrazione,"
            + " flag_tipo_mittente, text_oggetto, flag_tipo, data_documento,"
            + " tipo_documento_id, aoo_id, registro_id, utente_protocollatore_id,"
            + " ufficio_protocollatore_id, desc_denominazione_mittente,"
            + " desc_cognome_mittente, desc_nome_mittente, indi_mittente, indi_cap_mittente,"
            + " indi_localita_mittente, indi_provincia_mittente, annotazione_chiave,"
            + " annotazione_posizione, annotazione_descrizione, titolario_id, row_created_user,"
            + " flag_riservato, ufficio_mittente_id, utente_mittente_id,"
            + " nume_protocollo_mittente, data_ricezione, documento_id,"
            + " stato_protocollo, flag_mozione,num_prot_emergenza,data_annullamento, text_provvedimento_annullament,"
            + " text_nota_annullamento,data_effettiva_registrazione, row_created_time, versione, registro_anno_numero) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
            + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static String UPDATE_PROTOCOLLO = "UPDATE protocolli SET"
            + " flag_tipo_mittente = ?, text_oggetto = ?, "
            + " desc_denominazione_mittente = ?, desc_cognome_mittente = ?,"
            + " desc_nome_mittente = ?, indi_mittente = ?, indi_cap_mittente = ?,"
            + " indi_localita_mittente = ?, indi_provincia_mittente = ?,"
            + " annotazione_chiave = ?, annotazione_posizione = ?, annotazione_descrizione = ?,"
            + " titolario_id = ?, row_created_user = ?, row_created_time = ?,"
            + " ufficio_mittente_id = ?, utente_mittente_id = ?, nume_protocollo_mittente = ?,"
            + " data_ricezione = ?, data_documento = ?, documento_id = ?, stato_protocollo = ?, data_scarico=?,"
            + " num_prot_emergenza = ?, motivazione_modifica = ?"
            + " WHERE protocollo_id = ? AND versione = ?";

    public final static String SELECT_PROTOCOLLO = "SELECT * FROM PROTOCOLLI "
            + "WHERE ANNO_REGISTRAZIONE=? AND registro_id=? AND protocollo_id=?";

    public final static String SELECT_PROTOCOLLO_BY_NUMERO = "SELECT protocollo_id FROM PROTOCOLLI "
            + "WHERE ANNO_REGISTRAZIONE=? AND registro_id=? AND NUME_PROTOCOLLO=?";

    public final static String SELECT_PROTOCOLLO_BY_ID = "SELECT * FROM PROTOCOLLI WHERE protocollo_id =?";

    public final static String SELECT_LISTA_PROTOCOLLI_STORIA = "SELECT "
            + "p.protocollo_id,p.anno_registrazione,p.nume_protocollo,p.versione, p.row_created_user, p.row_created_time,"
            + "p.flag_tipo,p.flag_riservato,p.text_oggetto,p.data_registrazione,p.ufficio_protocollatore_id, p.utente_protocollatore_id,"
            + "p.documento_id,p.stato_protocollo,p.flag_tipo_mittente,"
            + "p.desc_cognome_mittente,p.desc_nome_mittente,"
            + "p.desc_denominazione_mittente,ut.nome as nome_assegnatario,"
            + "uf.descrizione as ufficio_assegnatario,"
            + "ut.cognome as cognome_assegnatario,"
            + "a.flag_competente,d.destinatario,d.flag_conoscenza "
            + "FROM protocolli p"
            + " LEFT JOIN protocollo_assegnatari a ON a.protocollo_id=p.protocollo_id"
            + " LEFT JOIN uffici uf ON a.ufficio_assegnatario_id=uf.ufficio_id"
            + " LEFT JOIN utenti ut ON a.utente_assegnatario_id=ut.utente_id"
            + " LEFT JOIN protocollo_destinatari d ON d.protocollo_id=p.protocollo_id "
            + "WHERE p.protocollo_id=?";

    // TODO:non utilizzata eliminare
    public final static String INSERT_ALLACCI = "INSERT INTO protocollo_allacci "
            + "(allaccio_id, protocollo_id, flag_principale, protocollo_allacciato_id, row_created_user, versione)"
            + " VALUES (?, ?, ?, ?, ?, ?)";

    public final static String PROTOCOLLO_ALLEGATI = "SELECT documento_id FROM protocollo_allegati WHERE protocollo_id = ?";

    public final static String PROTOCOLLO_ALLACCI = "SELECT a.*, p.nume_protocollo, p.anno_registrazione, p.flag_tipo"
            + " FROM protocolli p, protocollo_allacci a"
            + " WHERE a.protocollo_allacciato_id = p.protocollo_id and a.protocollo_id = ?";

    private final static String UPDATE_DOCUMENTO_ID = "UPDATE protocolli SET documento_id=? WHERE protocollo_id=?";

    public final static String INSERT_PROTOCOLLO_ALLEGATI = "INSERT INTO protocollo_allegati"
            + " (allegato_id, protocollo_id, documento_id, versione) VALUES (?, ?, ?, ?)";

    public final static String INSERT_PROTOCOLLO_ALLACCI = "INSERT INTO protocollo_allacci"
            + " (allaccio_id, protocollo_id, flag_principale, protocollo_allacciato_id, versione) VALUES (?, ?, ?, ?,(select versione from protocolli where protocollo_id=?))";

    private final static String ASSEGNATARIO_PER_COMPETENZA = "SELECT * FROM protocollo_assegnatari"
            + " WHERE flag_competente=1 AND protocollo_id = ?";

    private final static String PROTOCOLLO_ASSEGNATARI = "SELECT * FROM protocollo_assegnatari"
            + " WHERE protocollo_id = ?";

    private final static String INSERT_PROTOCOLLO_ASSEGNATARI = "INSERT INTO protocollo_assegnatari"
            + " (assegnatario_id, protocollo_id, data_assegnazione, data_operazione, "
            + "flag_competente, ufficio_assegnatario_id, utente_assegnatario_id, ufficio_assegnante_id, utente_assegnante_id, "
            + "stat_assegnazione, messaggio, versione) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static String UPDATE_MSG_ASSEGNATARIO_COMPETENTE = "UPDATE protocollo_assegnatari"
            + " set messaggio=? WHERE protocollo_id= ? AND flag_competente=1";

    public final static String UPDATE_SCARICO = "UPDATE protocolli SET stato_protocollo=?, data_scarico=?, row_created_time=?, row_created_user=?"
            + " WHERE protocollo_id=? AND versione=?";

    public final static String SELECT_LISTA_PROTOCOLLI = "SELECT "
            + "p.protocollo_id,p.anno_registrazione,p.nume_protocollo, p.registro_id,"
            + "p.flag_tipo,p.flag_riservato,p.text_oggetto,p.data_registrazione,"
            + "p.documento_id,p.stato_protocollo,p.flag_tipo_mittente,"
            + "p.desc_cognome_mittente,p.desc_nome_mittente,p.titolario_id,"
            + "p.desc_denominazione_mittente,ut.nome as nome_assegnatario,"
            + "uf.descrizione as ufficio_assegnatario,"
            + "ut.cognome as cognome_assegnatario,"
            + "a.flag_competente,d.destinatario,d.flag_conoscenza "
            + "FROM protocolli p"
            + " LEFT JOIN protocollo_assegnatari a ON a.protocollo_id=p.protocollo_id"
            + " LEFT JOIN uffici uf ON a.ufficio_assegnatario_id=uf.ufficio_id"
            + " LEFT JOIN utenti ut ON a.utente_assegnatario_id=ut.utente_id"
            + " LEFT JOIN protocollo_destinatari d ON d.protocollo_id=p.protocollo_id "
            + "WHERE p.registro_id=?";

    

    public final static String SELECT_ALLACCI = "SELECT DISTINCT "
            + "p.protocollo_id,p.anno_registrazione,p.nume_protocollo,"
            + "p.flag_tipo,p.flag_riservato,p.text_oggetto,p.data_registrazione,"
            + "p.flag_tipo_mittente,p.desc_cognome_mittente,p.desc_nome_mittente,"
            + "p.desc_denominazione_mittente "
            + "FROM protocolli p"
            + " LEFT JOIN protocollo_assegnatari a ON a.protocollo_id=p.protocollo_id"
            + " LEFT JOIN uffici uf ON a.ufficio_assegnatario_id=uf.ufficio_id"
            + " LEFT JOIN utenti ut ON a.utente_assegnatario_id=ut.utente_id"
            + " LEFT JOIN protocollo_destinatari d ON d.protocollo_id=p.protocollo_id "
            + "WHERE p.registro_id=? AND p.protocollo_id!=? ";

    

    public final static String DELETE_DESTINATARI = "DELETE FROM protocollo_destinatari WHERE protocollo_id = ?";

    private final static String PROCEDIMENTI_PROTOCOLLO = "SELECT procedimento_id, numero, anno, oggetto "
            + "FROM procedimenti WHERE protocollo_id = ?";

    private final static String PROTOCOLLO_PROCEDIMENTI = "SELECT p.*, r.anno, r.numero,r.oggetto FROM protocollo_procedimenti p, procedimenti r "
            + "WHERE p.procedimento_id =r.procedimento_id and p.protocollo_id = ?";

    private static ResourceBundle bundle;
    
    private static String SYSTEM_PARAMS = "systemParams";
}