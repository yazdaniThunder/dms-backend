package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.request.LonginRequestDto;
import com.sima.dms.service.LDAPService;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

@Service
public class LDAPServiceImpl implements LDAPService {

    @Override
    public boolean authUser(LonginRequestDto longinRequestDto) {

        try {
            Hashtable<String, String> environment = new Hashtable<String, String>();

            environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            environment.put(Context.PROVIDER_URL, "ldap://10.8.8.21:389");
            environment.put(Context.SECURITY_AUTHENTICATION, "simple");
            environment.put(Context.SECURITY_PRINCIPAL, "CN=Tondar-Hasheminejad Mohamadhossein,OU=Tondar,OU=3rd Parties,OU=IT-Div,DC=internal,DC=test01,DC=com");

//            DC=internal,DC=test01,DC=com"
            environment.put(Context.SECURITY_CREDENTIALS, "QAZwsx456");

            LdapContext ctx = new InitialLdapContext(environment, null);
            ctx.setRequestControls(null);
            NamingEnumeration<?> namingEnum = ctx.search("DC=internal,DC=test01,DC=com", "(objectclass=user)", getSimpleSearchControls());

            while (namingEnum.hasMore()) {
                SearchResult result = (SearchResult) namingEnum.next();
                Attributes attrs = result.getAttributes();
                String sAMAccountName = attrs.get("sAMAccountName").toString().toLowerCase();
                if (sAMAccountName.contains(longinRequestDto.getUsername().toLowerCase())) {

                    String cn = attrs.get("cn").toString().split(":")[1].trim();
                    String distinguishedName = attrs.get("distinguishedName").toString().split(cn)[1];

                    environment.put(Context.SECURITY_PRINCIPAL, "CN=" + cn + distinguishedName);
                    environment.put(Context.SECURITY_CREDENTIALS, longinRequestDto.getPassword());
                    DirContext context = new InitialDirContext(environment);
                    context.close();

//                    System.out.println(cn);
//                    System.out.println(distinguishedName);
//                    System.out.println(attrs.get("distinguishedName"));

                    return true;
                }
            }
            namingEnum.close();

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static SearchControls getSimpleSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(30000);
        return searchControls;
    }
}
