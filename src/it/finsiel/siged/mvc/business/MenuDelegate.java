package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Menu;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.MenuDAO;
import it.finsiel.siged.mvc.vo.organizzazione.MenuVO;

import java.util.Collection;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class MenuDelegate {

    private static Logger logger = Logger.getLogger(MenuDelegate.class
            .getName());

    private MenuDAO menuDAO = null;

    /**
	 * @return the menuDAO
	 */
	public MenuDAO getMenuDAO() {
		return menuDAO;
	}

	private static MenuDelegate delegate = null;

    private ServletConfig config = null;

    private MenuDelegate() {
        // Connect to DAO
        try {
            if (menuDAO == null) {
                menuDAO = (MenuDAO) DAOFactory.getDAO(Constants.MENU_DAO_CLASS);
                logger.info("MenuDAO instantiated:" + Constants.MENU_DAO_CLASS);
            }
        } catch (Exception e) {
            logger.error("Exception while connecting to MenuDAOjdbc!!", e);
        }

    }

    public static MenuDelegate getInstance() {
        if (delegate == null)
            delegate = new MenuDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.MENU_DELEGATE;
    }

    public Menu getRootMenu() {
        Organizzazione org = Organizzazione.getInstance();
        MenuVO vo = new MenuVO();
        vo.setId(0);
        Menu rootMenu = new Menu(vo);
        Collection menuVOList = null;
        try {
            menuVOList = menuDAO.getAllMenu();
        } catch (DataException e) {
            e.printStackTrace();
        }
        if (menuVOList != null && menuVOList.size() > 0) {
            MenuVO[] menus = new MenuVO[menuVOList.size()];
            menuVOList.toArray(menus);
            for (int i = 0; i < menus.length; i++) {
                org.addMenu(new Menu(menus[i]));
            }
            for (int i = 0; i < menus.length; i++) {
                MenuVO voi = menus[i];
                Menu mi = org.getMenu(voi.getId());
                if (voi.getParentId() == 0) {
                    mi.setParent(rootMenu);
                } else {
                    for (int j = 0; j < menus.length; j++) {
                        MenuVO voj = menus[j];
                        Menu mj = org.getMenu(voj.getId());
                        if (voi.getParentId() == voj.getId().intValue()) {
                            mi.setParent(mj);
                            break;
                        }
                    }
                }
            }
        }
        return rootMenu;
    }

    public boolean isUserEnabled(Utente utente, Menu menu) {
        try {
            return menuDAO.isUserEnabled(utente.getValueObject().getId()
                    .intValue(), utente.getUfficioInUso(), menu
                    .getValueObject().getId().intValue());
        } catch (DataException de) {
            logger.error("MenuDelegate: failed getting isUserEnabled: ");
            return false;
        }
    }

}