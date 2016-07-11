package org.company.techtest.site;

import org.restberrypi.core.jaxrs.annotations.Path;
import org.restberrypi.core.jaxrs.annotations.Permissions;
import org.restberrypi.core.security.Constants;

@Path(Constants.PAGE_1_URL)
@Permissions(Constants.ROLE_PAGE1)
public class Page1 extends LoggedPage {
}
