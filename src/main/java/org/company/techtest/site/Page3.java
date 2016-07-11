package org.company.techtest.site;

import org.restberrypi.core.jaxrs.annotations.Path;
import org.restberrypi.core.jaxrs.annotations.Permissions;
import org.restberrypi.core.security.Constants;

@Path(Constants.PAGE_3_URL)
@Permissions(Constants.ROLE_PAGE3)
public class Page3 extends LoggedPage {
}
