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

package it.flosslab.mvc.vo;

public class OggettoVO {
	
	private String id;
	private String descrizione;
	
	public OggettoVO(String id, String descrizione){
		this.id = id;
		this.descrizione = descrizione;
	}
	
	public OggettoVO(){
	}

	public String getId() {
		if(id != null){
			return id.toString();
		}return null;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setId(Integer id) {
		this.id = id.toString();
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
