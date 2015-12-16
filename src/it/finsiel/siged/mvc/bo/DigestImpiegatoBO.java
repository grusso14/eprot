package it.finsiel.siged.mvc.bo;
import it.finsiel.siged.mvc.vo.ImpiegatiVO;
import it.finsiel.siged.mvc.vo.ImpiegatoVO;

import java.util.Vector;

import org.apache.commons.digester.Digester;
public class DigestImpiegatoBO {
    
    
      Vector impiegati; 
      public DigestImpiegatoBO(){
          impiegati= new Vector();}


      
      public void digest(String fileXML) {
        try {
          Digester digester = new Digester();
          //Push the current object onto the stack
          digester.push(this); //Creates a new instance of the Student class
          digester.addObjectCreate( "impiegati", ImpiegatiVO.class ); 
          digester.addObjectCreate( "impiegati/impiegato", ImpiegatoVO.class ); 
          //setName method of the Student instance
          //Uses tag name as the property name
          digester.addBeanPropertySetter( "impiegati/impiegato/matricola","matricola"); 
          //Uses setCourse method of the Student instance
          //Explicitly specify property name as 'course'
          digester.addBeanPropertySetter("impiegati/impiegato/nome","nome");
          digester.addBeanPropertySetter("impiegati/impiegato/cognome","cognome");
          digester.addBeanPropertySetter("impiegati/impiegato/data_nascita","dataascita");
          digester.addBeanPropertySetter("impiegati/impiegato/comune","comune");
          digester.addBeanPropertySetter("impiegati/impiegato/provincia","provincia");
          digester.addBeanPropertySetter("impiegati/impiegato/qualifica","qualifica");
          //Move to next employe
          digester.addSetNext( "impiegati/impiegato", "addImpegato" ); 
          //Print the contents of the Vector
          
          
          DigestImpiegatoBO ds = (DigestImpiegatoBO) 
                       digester.parse("/eprot/impiegato.xml"); 
      
          
        }
        catch (Exception ex) {
          ex.printStackTrace();
          }
      }  


      public void addImpiegati( ImpiegatoVO imp ) {
        //Add a new Student instance to the Vector
        impiegati.add( imp );    
      }  
    }

     

    




