package org.company.techtest.site;

import org.company.core.jaxrs.annotations.Path;
import org.company.core.jaxrs.annotations.Permissions;
import org.company.core.security.Constants;

@Path(Constants.PAGE_2_URL)
@Permissions(Constants.ROLE_PAGE2)
public class Page2 extends LoggedPage {
}
