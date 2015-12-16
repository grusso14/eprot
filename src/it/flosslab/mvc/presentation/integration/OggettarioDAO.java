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

package it.flosslab.mvc.presentation.integration;

import it.finsiel.siged.exception.DataException;
import it.flosslab.mvc.vo.OggettoVO;

import java.sql.Connection;
import java.util.Collection;

/*
 * @author G.Calli.
 */

public interface OggettarioDAO {

    public OggettoVO newOggetto(Connection conn, OggettoVO oggettoVO)
            throws DataException;

    public void deleteOggetto(Connection conn, int id)throws DataException;
    
    public Collection getOggetti() throws DataException;
}