package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.MenuDAO;
import it.finsiel.siged.mvc.vo.organizzazione.MenuVO;
import it.finsiel.siged.rdb.JDBCManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

public class MenuDAOjdbc implements MenuDAO {
    private final static String SELECT_MENU = "SELECT * FROM MENU";

    private final static String SELECT_PERMESSI = "SELECT COUNT(*) FROM permessi_utente WHERE utente_id = ? AND ufficio_id = ? AND menu_id = ?";

    
    private final static String SELECT_PERMESSI_LETTURA ="SELECT COUNT(*) FROM PERMESSI_LETTURA_FUNZIONE WHERE user_id = ?  AND menu_id = ?";
    
    static Logger logger = Logger.getLogger(MenuDAOjdbc.class.getName());

    private JDBCManager jdbcMan = new JDBCManager();

    /**
     * @return a Collection of MenuVO
     * @throws DataException
     */
    public Collection getAllMenu() throws DataException {
        ArrayList menuList = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_MENU);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                MenuVO menu = new MenuVO();
                menu.setId(rs.getInt("menu_id"));
                menu.setTitle(rs.getString("titolo"));
                menu.setDescription(rs.getString("descrizione"));
                menu.setPosition(rs.getInt("posizione"));
                menu.setParentId(rs.getInt("parent_id"));
                menu.setLink(rs.getString("link"));
                menuList.add(menu);

            }
        } catch (Exception e) {
            logger.error("Load Menu ", e);
            throw new DataException("Cannot load the menu");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }

        return menuList;
    }

    public boolean isUserEnabled(int utenteId, int ufficioId, int menuId)
            throws DataException {
        boolean enabled = false;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_PERMESSI);
            pstmt.setInt(1, utenteId);
            pstmt.setInt(2, ufficioId);
            pstmt.setInt(3, menuId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                enabled = rs.getInt(1) == 1;
            }
            if(!enabled){
            	jdbcMan.closeAll(rs, pstmt, connection);
            	connection = jdbcMan.getConnection();
            	pstmt = connection.prepareStatement(SELECT_PERMESSI_LETTURA);
                pstmt.setInt(1, utenteId);
                pstmt.setInt(2, menuId);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    enabled = rs.getInt(1) == 1;
                }
            }
        } catch (Exception e) {
            logger.error("Load Permission ", e);
            throw new DataException("Cannot load permission");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }

        return enabled;
    }
}