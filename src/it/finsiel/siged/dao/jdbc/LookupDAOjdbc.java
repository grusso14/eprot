package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.AreaOrganizzativa;
import it.finsiel.siged.mvc.integration.LookupDAO;
import it.finsiel.siged.mvc.vo.IdentityVO;
import it.finsiel.siged.mvc.vo.lookup.ProvinciaVO;
import it.finsiel.siged.mvc.vo.lookup.TipoDocumentoVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.flosslab.mvc.vo.OggettoVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class LookupDAOjdbc implements LookupDAO {
    static Logger logger = Logger.getLogger(LookupDAOjdbc.class.getName());

    private JDBCManager jdbcMan = new JDBCManager();

    public Collection getProvince() throws DataException {
        ArrayList province = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_PROVINCE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                province.add(new ProvinciaVO(rs.getLong("provincia_id"), rs
                        .getString("DESC_PROVINCIA")));
            }
        } catch (Exception e) {
            logger.error("Load Province", e);
            throw new DataException("Cannot load Province");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return province;
    }

    public Map getTipiDocumento(Collection aoo) throws DataException {
        ArrayList tipiDocAoo;
        Map tipiDoc = new HashMap();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            Iterator it = aoo.iterator();
            while (it.hasNext()) {
                tipiDocAoo = new ArrayList();
                AreaOrganizzativa areaOrg = (AreaOrganizzativa) it.next();

                pstmt = connection.prepareStatement(SELECT_TIPI_DOCUMENTI);
                pstmt.setInt(1, areaOrg.getValueObject().getId().intValue());
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    tipiDocAoo.add(new TipoDocumentoVO(rs
                            .getInt("tipo_documento_id"), rs
                            .getString("DESC_TIPO_DOCUMENTO")));
                }
                jdbcMan.close(pstmt);
                if (tipiDocAoo != null && tipiDocAoo.size() > 0)
                    tipiDoc.put(areaOrg.getValueObject().getId(), tipiDocAoo);
            }
        } catch (Exception e) {
            logger.error("Load Tipi documenti", e);
            throw new DataException("Cannot load the Tipi documenti");
        } finally {
            jdbcMan.close(rs);

            jdbcMan.close(connection);
        }
        return tipiDoc;
    }

    public Map getTipiProcedimento(Collection aoo) throws DataException {
        ArrayList tipiProcAoo;
        Map tipiProc = new HashMap();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            Iterator it = aoo.iterator();
            while (it.hasNext()) {
                tipiProcAoo = new ArrayList();
                AreaOrganizzativa areaOrg = (AreaOrganizzativa) it.next();

                pstmt = connection.prepareStatement(SELECT_TIPI_PROCEDIMENTO);
                // pstmt.setInt(1, areaOrg.getValueObject().getId().intValue());
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    tipiProcAoo.add(new TipoDocumentoVO(rs
                            .getInt("tipo_procedimenti_id"), rs
                            .getString("tipo")));
                }
                jdbcMan.close(pstmt);
                if (tipiProcAoo != null && tipiProcAoo.size() > 0)
                    tipiProc.put(areaOrg.getValueObject().getId(), tipiProcAoo);
            }
        } catch (Exception e) {
            logger.error("Load Tipi procediumenti", e);
            throw new DataException("Cannot load the Tipi procedimenti");
        } finally {
            jdbcMan.close(rs);

            jdbcMan.close(connection);
        }
        return tipiProc;
    }

    public IdentityVO getMezzoSpedizione(int mezzoId) throws DataException {
        IdentityVO mezzo = new IdentityVO();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_MEZZO_SPEDIZIONE_DA_ID);
            pstmt.setInt(1, mezzoId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                mezzo.setId(rs.getInt("spedizioni_id"));
                mezzo.setCodice(rs.getString("codi_spedizione"));
                mezzo.setDescription(rs.getString("desc_spedizione"));
            }
            jdbcMan.close(pstmt);
        } catch (Exception e) {
            logger.error("Load Mezzo di Spedizione", e);
            throw new DataException("Cannot load Mezzo di Spedizione");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(connection);
        }
        return mezzo;

    }

    public IdentityVO getMezzoSpedizioneDaMezzo(String mezzo)
            throws DataException {
        IdentityVO mezzoVO = new IdentityVO();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection
                    .prepareStatement(SELECT_MEZZO_SPEDIZIONE_DA_STRING);
            pstmt.setString(1, mezzo);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                mezzoVO.setId(rs.getInt("spedizioni_id"));
                mezzoVO.setCodice(rs.getString("codi_spedizione"));
                mezzoVO.setDescription(rs.getString("desc_spedizione"));
            }
            jdbcMan.close(pstmt);
        } catch (Exception e) {
            logger.error("Load Mezzo di Spedizione", e);
            throw new DataException("Cannot load Mezzo di Spedizione");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(connection);
        }
        return mezzoVO;

    }

    public Map getMezziSpedizione(Collection aoo) throws DataException {
        ArrayList mezziAoo;
        Map mezzi = new HashMap();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            Iterator it = aoo.iterator();
            while (it.hasNext()) {
                mezziAoo = new ArrayList();
                AreaOrganizzativa areaOrg = (AreaOrganizzativa) it.next();

                pstmt = connection.prepareStatement(SELECT_MEZZI_SPEDIZIONE);
                pstmt.setInt(1, areaOrg.getValueObject().getId().intValue());
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    IdentityVO id = new IdentityVO();
                    id.setId(rs.getInt("spedizioni_id"));
                    id.setCodice(rs.getString("codi_spedizione"));
                    id.setDescription(rs.getString("desc_spedizione"));
                    mezziAoo.add(id);
                }
                jdbcMan.close(pstmt);
                if (mezziAoo != null && mezziAoo.size() > 0)
                    mezzi.put(areaOrg.getValueObject().getId(), mezziAoo);
            }
        } catch (Exception e) {
            logger.error("Load Mezzi di Spedizione", e);
            throw new DataException("Cannot load the Mezzi di Spedizione");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(connection);
        }
        return mezzi;
    }

    public Collection getTitoliDestinatario() throws DataException {

        Collection listaTitoli = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_TITOLIDESTINATARI);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                IdentityVO titoloVO = new IdentityVO();
                titoloVO.setId(rs.getInt("id"));
                titoloVO.setDescription(rs.getString("descrizione"));
                listaTitoli.add(titoloVO);
            }
        } catch (Exception e) {
            logger.error("Load getTitoliDestinatari", e);
            throw new DataException("Cannot load getElencoTitoliDestinatari");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return listaTitoli;

    }



    
    private final static String SELECT_TITOLIDESTINATARI = "SELECT * FROM titoli_destinatari "
            + " ORDER BY descrizione";

    public final static String SELECT_MEZZI_SPEDIZIONE = "SELECT * FROM SPEDIZIONI where aoo_id=? and flag_abilitato=1 ORDER BY desc_spedizione";

    public final static String SELECT_MEZZO_SPEDIZIONE_DA_ID = "SELECT * FROM SPEDIZIONI where spedizioni_id=?";

    public final static String SELECT_MEZZO_SPEDIZIONE_DA_STRING = "SELECT * FROM SPEDIZIONI where desc_spedizione=?";

    public final static String SELECT_TIPI_DOCUMENTI = "SELECT tipo_documento_id, DESC_TIPO_DOCUMENTO, aoo_id FROM TIPI_DOCUMENTO WHERE aoo_id=? ORDER BY aoo_id, DESC_TIPO_DOCUMENTO";

    public final static String SELECT_TIPI_PROCEDIMENTO = "SELECT * FROM TIPI_PROCEDIMENTO ORDER BY tipo";

    public final static String SELECT_PROVINCE = "SELECT provincia_id, DESC_PROVINCIA FROM PROVINCE ORDER BY DESC_PROVINCIA";

	
};