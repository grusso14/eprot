/*
*
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
*
* This file is part of e-prot 1.1 software.
* e-prot 1.1 is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
* Version: e-prot 1.1
*/

package it.flosslab.dao.helper;

import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.DateUtil;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class ProtocolloDaoHelper {

	/**
	 * @param protocollo
	 * @param rs
	 * @throws SQLException
	 */
	public static void fillProtocolloVOFromRecord(ProtocolloVO protocollo, ResultSet rs)
			throws SQLException {
		protocollo.setId(rs.getInt("protocollo_id"));
		protocollo.setRowCreatedTime(rs.getTimestamp("row_created_time"));
		protocollo.setRowCreatedUser(rs.getString("row_created_user"));
		protocollo.setVersione(rs.getInt("versione"));
		protocollo.setAnnoRegistrazione(rs.getInt("anno_registrazione"));
		protocollo.setAooId(rs.getInt("aoo_id"));
		protocollo.setChiaveAnnotazione(rs.getString("annotazione_chiave"));
		protocollo.setCodDocumentoDoc(rs.getString("codi_documento_doc"));
		protocollo.setCognomeMittente(rs.getString("desc_cognome_mittente"));
		protocollo.setDataAnnullamento(rs.getDate("data_annullamento"));
		protocollo.setDataDocumento(rs.getDate("data_documento"));
		protocollo.setDataEffettivaRegistrazione(rs.getDate("data_effettiva_registrazione"));
		protocollo.setDataProtocolloMittente(rs.getString("data_protocollo_mittente"));
		protocollo.setDataRegistrazione(rs.getTimestamp("data_registrazione"));
		protocollo.setDataRicezione(rs.getTimestamp("data_ricezione"));
		protocollo.setDataScadenza(rs.getDate("data_scadenza"));
		protocollo.setDataScarico(rs.getDate("data_scarico"));
		protocollo.setDenominazioneMittente(rs.getString("desc_denominazione_mittente"));
		protocollo.setDescrizioneAnnotazione(rs.getString("annotazione_descrizione"));
		protocollo.setDocumentoPrincipaleId(rs.getInt("documento_id"));
		protocollo.setMozione(rs.getInt("flag_mozione") == 1);
		protocollo.setRiservato(rs.getInt("flag_riservato") == 1);
		protocollo.setFlagTipo(rs.getString("flag_tipo"));
		protocollo.setFlagTipoMittente(rs.getString("flag_tipo_mittente"));
		protocollo.setMittenteCap(rs.getString("indi_cap_mittente"));
		protocollo.setMittenteComune(rs.getString("indi_localita_mittente"));
		protocollo.setMittenteIndirizzo(rs.getString("indi_mittente"));
		protocollo.setMittenteNazione(rs.getString("indi_nazione_mittente"));
		protocollo.setMittenteProvinciaId(rs.getInt("indi_provincia_mittente"));
		protocollo.setNomeMittente(rs.getString("desc_nome_mittente"));
		protocollo.setNotaAnnullamento(rs.getString("text_nota_annullamento"));
		protocollo.setNumProtocollo(rs.getInt("nume_protocollo"));
		protocollo.setNumProtocolloMittente(rs.getString("nume_protocollo_mittente"));
		protocollo.setOggetto(rs.getString("text_oggetto"));
		protocollo.setPosizioneAnnotazione(rs.getString("annotazione_posizione"));
		protocollo.setProvvedimentoAnnullamento(rs.getString("text_provvedimento_annullament"));
		protocollo.setRegistroId(rs.getInt("registro_id"));
		protocollo.setStatoProtocollo(rs.getString("stato_protocollo"));
		protocollo.setTipoDocumentoId(rs.getInt("tipo_documento_id"));
		protocollo.setTitolarioId(rs.getInt("titolario_id"));
		protocollo.setUfficioMittenteId(rs.getInt("ufficio_mittente_id"));
		protocollo.setUfficioProtocollatoreId(rs.getInt("ufficio_protocollatore_id"));
		protocollo.setUtenteMittenteId(rs.getInt("utente_mittente_id"));
		protocollo.setUtenteProtocollatoreId(rs.getInt("utente_protocollatore_id"));
		protocollo.setNumProtocolloEmergenza(rs.getInt("num_prot_emergenza"));
		protocollo.setRowUpdatedUser(rs.getString("row_created_user"));
		protocollo.setMotivazione(rs.getString("motivazione_modifica"));
		
		protocollo.setReturnValue(ReturnValues.FOUND);
	}
	
	
	/**
	 * @param protocollo
	 * @param pstmt
	 * @throws SQLException
	 */
	public static void createStatementForNewProtocollo(ProtocolloVO protocollo,PreparedStatement pstmt) throws SQLException {
		pstmt.setInt(1, protocollo.getId().intValue()); // protocollo_id
		pstmt.setInt(2, protocollo.getAnnoRegistrazione()); // ANNO_REGISTRAZIONE
		pstmt.setLong(3, protocollo.getNumProtocollo()); // NUME_PROTOCOLLO
		pstmt.setTimestamp(4, new Timestamp(protocollo.getDataRegistrazione().getTime())); // data_registrazione
		pstmt.setString(5, protocollo.getFlagTipoMittente()); // FLAG_TIPO_MITTENTE
		pstmt.setString(6, protocollo.getOggetto()); // TEXT_OGGETTO
		pstmt.setString(7, protocollo.getFlagTipo()); // FLAG_TIPO
		if (protocollo.getDataDocumento() == null) {
		    pstmt.setNull(8, Types.DATE); // DATA_DOCUMENTO
		} else {
		    pstmt.setDate(8, new Date(protocollo.getDataDocumento().getTime())); // DATA_DOCUMENTO
		}
		pstmt.setLong(9, protocollo.getTipoDocumentoId()); // tipo_documento_id
		pstmt.setInt(10, protocollo.getAooId()); // aoo_id
		pstmt.setInt(11, protocollo.getRegistroId()); // registro_id
		pstmt.setInt(12, protocollo.getUtenteProtocollatoreId()); // utente_protocollatore_id
		pstmt.setInt(13, protocollo.getUfficioProtocollatoreId()); // ufficio_protocollatore_id
		pstmt.setString(14, protocollo.getDenominazioneMittente()); // DESC_DENOMINAZIONE_MITTENTE
		pstmt.setString(15, protocollo.getCognomeMittente()); // DESC_COGNOME_MITTENTE
		pstmt.setString(16, protocollo.getNomeMittente()); // DESC_NOME_MITTENTE
		pstmt.setString(17, protocollo.getMittenteIndirizzo()); // INDI_MITTENTE
		pstmt.setString(18, protocollo.getMittenteCap()); // INDI_CAP_MITTENTE
		pstmt.setString(19, protocollo.getMittenteComune()); // INDI_LOCALITA_MITTENTE
		pstmt.setInt(20, protocollo.getMittenteProvinciaId()); // INDI_PROVINCIA_MITTENTE
		pstmt.setString(21, protocollo.getChiaveAnnotazione()); // ANNOTAZIONE_CHIAVE
		pstmt.setString(22, protocollo.getPosizioneAnnotazione()); // ANNOTAZIONE_POSIZIONE
		pstmt.setString(23, protocollo.getDescrizioneAnnotazione()); // ANNOTAZIONE_DESCRIZIONE
		if (protocollo.getTitolarioId() == 0) {
		    pstmt.setNull(24, Types.INTEGER);
		} else {
		    pstmt.setInt(24, protocollo.getTitolarioId());
		}
		pstmt.setString(25, protocollo.getRowCreatedUser()); // ROW_CREATED_USER
		pstmt.setInt(26, protocollo.isRiservato() ? 1 : 0); // FLAG_RISERVATO
		if (!"I".equals(protocollo.getFlagTipo())) {
		    pstmt.setInt(27, protocollo.getUfficioMittenteId());
		    if (protocollo.getUtenteMittenteId() == 0) {
		        pstmt.setNull(28, Types.INTEGER);
		    } else {
		        pstmt.setInt(28, protocollo.getUtenteMittenteId());
		    }
		    pstmt.setNull(29, Types.VARCHAR);
		} else {
		    pstmt.setNull(27, Types.INTEGER);
		    pstmt.setNull(28, Types.INTEGER);
		    pstmt.setString(29, protocollo.getNumProtocolloMittente());
		}
		if (protocollo.getDataRicezione() == null) {
		    pstmt.setNull(30, Types.DATE); // DATA_RICEZIONE
		} else {
		    pstmt.setTimestamp(30, new Timestamp(protocollo.getDataRicezione().getTime())); // DATA_DOCUMENTO
		}
		if (protocollo.getDocumentoPrincipaleId() == null) {
		    pstmt.setNull(31, Types.INTEGER); // DATA_DOCUMENTO
		} else {
		    pstmt.setInt(31, protocollo.getDocumentoPrincipaleId().intValue()); // DOCUMENTO_ID
		}
		pstmt.setString(32, protocollo.getStatoProtocollo());
		pstmt.setInt(33, protocollo.isMozione() ? 1 : 0);
		pstmt.setInt(34, protocollo.getNumProtocolloEmergenza());
		if (protocollo.getNumProtocolloEmergenza() > 0) {
		    if (protocollo.getDataAnnullamento() == null) {
		        pstmt.setNull(35, Types.DATE); // data_Annullamento
		    } else {
		        pstmt.setDate(35, new Date(protocollo.getDataAnnullamento().getTime())); // data_Annullamento
		    }
		    pstmt.setString(36, protocollo.getProvvedimentoAnnullamento());
		    pstmt.setString(37, protocollo.getNotaAnnullamento());
		} else {
		    pstmt.setNull(35, Types.DATE); // data_Annullamento
		    pstmt.setString(36, null);
		    pstmt.setString(37, null);
		}
		pstmt.setTimestamp(38, new Timestamp(System.currentTimeMillis())); // DATA_EFFETTIVA_REGISTRAZIONE
		pstmt.setTimestamp(39, new Timestamp(System.currentTimeMillis())); // ROW_CREATED_TIME
		pstmt.setInt(40, protocollo.getVersione());
		pstmt.setLong(41, protocollo.getKey());
	}
	
	
	/**
	 * @param protocollo
	 * @param rs
	 * @throws SQLException
	 */
	public static void fillReportProtocolloViewFromRecord(
			ReportProtocolloView protocollo, ResultSet rs) throws SQLException {
		protocollo.setProtocolloId(rs.getInt("protocollo_id"));
		protocollo.setAnnoProtocollo(rs.getInt("anno_registrazione"));
		protocollo.setNumeroProtocollo(rs.getInt("nume_protocollo"));
		protocollo.setTipoProtocollo(rs.getString("flag_tipo"));
		if (rs.getBoolean("flag_riservato")) {
		    protocollo.setOggetto(Parametri.PROTOCOLLO_RISERVATO);
		    protocollo.setMittente(Parametri.PROTOCOLLO_RISERVATO);
		} else {
		    protocollo.setOggetto(rs.getString("text_oggetto"));
		    StringBuffer mittente = new StringBuffer();
		    if ("F".equals(rs.getString("flag_tipo_mittente"))) {
		        mittente.append(rs.getString("desc_cognome_mittente"));
		        if (rs.getString("desc_nome_mittente") != null) {
		            mittente.append(' ').append(
		                    rs.getString("desc_nome_mittente"));
		        }
		    } else {
		        mittente.append(rs
		                .getString("desc_denominazione_mittente"));
		    }
		    protocollo.setMittente(mittente.toString());
		}
		protocollo.setDataProtocollo(DateUtil.formattaData(rs.getDate(
		        "data_registrazione").getTime()));
		protocollo.setPdf(rs.getInt("documento_id") > 0);
		protocollo.setDocumentoId(rs.getInt("documento_id"));
		protocollo.setStatoProtocollo(rs.getString("stato_protocollo"));
	}
	
	
	/**
	 * @param protocollo
	 * @param pstmt
	 * @return
	 * @throws SQLException
	 */
	public static void createStatementToUpdateProtocollo(ProtocolloVO protocollo,
			PreparedStatement pstmt) throws SQLException {
		int n = 0;
		pstmt.setString(++n, protocollo.getFlagTipoMittente()); // flag_tipo_mittente
		pstmt.setString(++n, protocollo.getOggetto()); // text_oggetto
		pstmt.setString(++n, protocollo.getDenominazioneMittente()); // desc_denominazione_mittente
		pstmt.setString(++n, protocollo.getCognomeMittente()); // desc_cognome_mittente
		pstmt.setString(++n, protocollo.getNomeMittente()); // desc_nome_mittente
		pstmt.setString(++n, protocollo.getMittenteIndirizzo()); // indi_mittente
		pstmt.setString(++n, protocollo.getMittenteCap()); // indi_cap_mittente
		pstmt.setString(++n, protocollo.getMittenteComune()); // indi_localita_mittente
		pstmt.setInt(++n, protocollo.getMittenteProvinciaId()); // indi_provincia_mittente
		pstmt.setString(++n, protocollo.getChiaveAnnotazione()); // annotazione_chiave
		pstmt.setString(++n, protocollo.getPosizioneAnnotazione()); // annotazione_posizione
		pstmt.setString(++n, protocollo.getDescrizioneAnnotazione()); // annotazione_descrizione
		if (protocollo.getTitolarioId() == 0) { // titolario_id
		    pstmt.setNull(++n, Types.INTEGER);
		} else {
		    pstmt.setInt(++n, protocollo.getTitolarioId());
		}
		pstmt.setString(++n, protocollo.getRowUpdatedUser()); // row_created_user
		pstmt.setTimestamp(++n, new Timestamp(System
		        .currentTimeMillis())); // row_created_user
		if (!"I".equals(protocollo.getFlagTipo())) {
		    pstmt.setInt(++n, protocollo.getUfficioMittenteId()); // ufficio_mittente_id
		    if (protocollo.getUtenteMittenteId() == 0) { // utente_mittente_id
		        pstmt.setNull(++n, Types.INTEGER);
		    } else {
		        pstmt.setInt(++n, protocollo.getUtenteMittenteId());
		    }
		    pstmt.setNull(++n, Types.VARCHAR); // nume_protocollo_mittente
		} else {
		    pstmt.setNull(++n, Types.INTEGER);
		    pstmt.setNull(++n, Types.INTEGER);
		    pstmt.setString(++n, protocollo.getNumProtocolloMittente());
		}
		if (protocollo.getDataRicezione() == null) {
		    pstmt.setNull(++n, Types.DATE); // data_ricezione
		} else {
		    pstmt.setDate(++n, new Date(protocollo.getDataRicezione()
		            .getTime())); // data_ricezione
		}
		if (protocollo.getDataDocumento() == null) {
		    pstmt.setNull(++n, Types.DATE); // data_documento
		} else {
		    pstmt.setDate(++n, new Date(protocollo.getDataDocumento()
		            .getTime())); // data_documento
		}
		if (protocollo.getDocumentoPrincipaleId() == null) {
		    pstmt.setNull(++n, Types.INTEGER); // documento_id
		} else {
		    pstmt.setInt(++n, protocollo.getDocumentoPrincipaleId()
		            .intValue()); // documento_id
		}
		pstmt.setString(++n, protocollo.getStatoProtocollo()); // stato_protocollo
		if (protocollo.getDataScarico() == null) {
		    pstmt.setNull(++n, Types.DATE); // data_scarico
		} else {

		    pstmt.setDate(++n, new Date(protocollo.getDataScarico()
		            .getTime())); // data_scarico
		}
		pstmt.setInt(++n, protocollo.getNumProtocolloEmergenza()); // Numero
		// Protocollo
		// Emergenza
		pstmt.setString(++n, protocollo.getMotivazione());
		pstmt.setInt(++n, protocollo.getId().intValue()); // protocollo_id
		pstmt.setInt(++n, protocollo.getVersione() + 1); // versione
		
	}
	

}
