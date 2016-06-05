package org.company.techtest.site;

import org.company.core.jaxrs.annotations.Path;
import org.company.core.jaxrs.annotations.Permissions;
import org.company.core.security.Constants;

@Path(Constants.INDEX_URL)
@Permissions({ Constants.ROLE_PAGE1, Constants.ROLE_PAGE2, Constants.ROLE_PAGE3 })
public class IndexPage extends LoggedPage {
}
