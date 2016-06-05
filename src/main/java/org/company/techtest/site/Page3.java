package org.company.techtest.site;

import org.company.core.jaxrs.annotations.Path;
import org.company.core.jaxrs.annotations.Permissions;
import org.company.core.security.Constants;

@Path(Constants.PAGE_3_URL)
@Permissions(Constants.ROLE_PAGE3)
public class Page3 extends LoggedPage {
}
