package com.ehpessoa.commons.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.InitialLdapContext;

/**
 * 
 * @author Everaldo Pessoa
 *
 */
public class AD {
	
	/**
	 * 
	 *
	 */
	public AD() {
		
	}
	
	/**
	 * @param host
	 * @param port
	 * @param login
	 * @param password
	 * @param dominio
	 * @param searchBase
	 * @return result
	 * @throws Exception 
	 */
	public ArrayList<Person> search(String host, String port, String login, String password,  
			String dominio, String searchBase) throws Exception {		
			
		ArrayList<Person> result = new ArrayList<Person>();
		try {

			// prepara parametros de conexao ao AD
			Hashtable<String, String> envDC = new Hashtable<String, String>();
			envDC.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
			envDC.put(Context.SECURITY_AUTHENTICATION, "simple");
			envDC.put(Context.SECURITY_PRINCIPAL, login+dominio);
			envDC.put(Context.SECURITY_CREDENTIALS, password);
			envDC.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);
			
			//	cria o objeto de contexto e executa a pesquisa
			LdapContext ldapContext = new InitialLdapContext(envDC, null);
			SearchControls searchCtls = new SearchControls();
			String[] returnedAtts = { "sAMAccountName", "title", "givenName", "sn", "displayName","mail", "manager", "memberOf" };
			searchCtls.setReturningAttributes(returnedAtts);
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			// Find by user
			String searchFilterFirstname = "(&(objectClass=user)(sAMAccountName="+login+"))";			
			NamingEnumeration<SearchResult> answerFirstName = ldapContext.search(searchBase, searchFilterFirstname,searchCtls);			
			while ( answerFirstName.hasMoreElements() ) {				
				SearchResult sr = answerFirstName.next();
				Attributes attrs = sr.getAttributes();
				// exibe os valores dos atributos retornados junto com seus valores				
				Person person = new Person();
				NamingEnumeration<String> enumeration = attrs.getIDs();
				while (enumeration.hasMoreElements()) {
					String attrId = enumeration.nextElement();
					String value = (String)attrs.get(attrId).get();
					if ( attrId.equalsIgnoreCase("sAMAccountName") ) {
						person.setUser(value); 
					} else if ( attrId.equalsIgnoreCase("title") ) {
						person.setTitle(value); 
					} else if ( attrId.equalsIgnoreCase("givenName") ) {
						person.setFirstname(value); 
					} else if ( attrId.equalsIgnoreCase("sn") ) {
						person.setLastname(value); 
					} else if ( attrId.equalsIgnoreCase("displayName") ) {
						person.setDisplayname(value); 
					} else if ( attrId.equalsIgnoreCase("mail") ) {
						person.setMail(value); 
					} else if ( attrId.equalsIgnoreCase("manager") ) {
						person.setManager(value); 
					} else if ( attrId.equalsIgnoreCase("memberOf") ) {
						person.setMemberOf(value); 
					}					
				}
				result.add(person);
			}
						
			return result;
			
		} catch (Exception e) {
			throw new Exception(e);
		}
		
	}
	
	

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Infos do Active Directory
		String host = args[0]; //host
		String port = args[1]; //389
		String login = args[2]; //network ID
		String password = args[3]; //password
		String dominio = args[4]; //domain
		String searchBase = args[5]; //base DN
		
		AD ldap = new AD();
		try {
			ArrayList<Person> coll = ldap.search(host, port, login, password, dominio, searchBase);
			Iterator<Person> it = coll.iterator();
			while ( it.hasNext() ) {
				Person p = it.next();
				System.out.println("user: " + p.getUser());
				System.out.println("title: " + p.getTitle());			
				System.out.println("first name: " + p.getFirstname());
				System.out.println("last name: " + p.getLastname());
				System.out.println("display name: " + p.getDisplayname());
				System.out.println("e-mail: " + p.getMail());
				System.out.println("manager: " + p.getManager());
				System.out.println("memberOf: " + p.getMemberOf());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}
