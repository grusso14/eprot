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
package it.flosslab.report.utility;

import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author roberto onnis
 *
 */
public class ReportGenerator {
	private String prot_num;
	private String data;
	private String data_doc;
	private String oggetto;
	private String ufficio;
	private List<String> mittenti;

	public ReportGenerator(ProtocolloIngressoForm form) {
		this.prot_num = form.getNumeroProtocollo();
		this.data = form.getDataRegistrazione();
		this.data_doc = form.getDataDocumento();
		this.oggetto = form.getOggetto();
		// TODO: vedere gli assegnatari e non l'ufficio corrente.
		this.ufficio = form.getUfficioCorrente().getName();
		this.mittenti = this.getMittentiAsString(form);
	}

	private List<String> getMittentiAsString(ProtocolloIngressoForm form) {
		List<String> mittentiAsString = new ArrayList<String>();
		if(form.getMittente().getTipo().equals("M")){
			List<SoggettoVO> mittenti = form.getMittenti();
			for(SoggettoVO mittente : mittenti){
				mittentiAsString.add(mittente.getNome() + " " +mittente.getCognome() + " - " +mittente.getIndirizzoCompleto());
			}
		}else if(form.getMittente().getTipo().equals("F")){
			SoggettoVO mittente = form.getMittente();
			mittentiAsString.add(mittente.getNome() + " " +mittente.getCognome() + " - " +mittente.getIndirizzoCompleto());
		}else if(form.getMittente().getTipo().equals("G")){
			SoggettoVO mittente = form.getMittente();
			mittentiAsString.add(mittente.getDescrizioneDitta() + " - " +mittente.getIndirizzoCompleto());
		}
		return mittentiAsString;
	}
	

	public HashMap<String, String> getParameters(){
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("prot_num", prot_num);
		parameters.put("data", data);
		parameters.put("data_doc", data_doc);
		parameters.put("oggetto", oggetto);
		parameters.put("ufficio", ufficio);
		int index=0;
		for(String mittente : mittenti){
			parameters.put("mittente_"+index, mittente);
			index++;
		}
		return parameters;
	}
}
