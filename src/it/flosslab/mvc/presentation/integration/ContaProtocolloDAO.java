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

import java.util.HashMap;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;

public interface ContaProtocolloDAO {
	 
	public int contaProtocolliAllacciabili(Utente utente, int annoProtocolo,
	            int numeroProtocolloDa, int numeroProtocolloA, int protocolloId)
	            throws DataException;
	
	
	public int contaProtocolliAssegnati(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date DataA,
            String statoProtocollo, String statoScarico,
            String tipoUtenteUfficio) throws DataException;
	
	
	public int contaProtocolli(Utente utente, Ufficio ufficio, HashMap sqlDB)
    throws DataException;
	
	public int contaProtocolliRespinti(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA) throws DataException;

}
