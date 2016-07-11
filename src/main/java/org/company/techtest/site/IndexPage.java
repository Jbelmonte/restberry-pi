package org.company.techtest.site;

import org.restberrypi.core.jaxrs.annotations.Path;
import org.restberrypi.core.jaxrs.annotations.Permissions;
import org.restberrypi.core.security.Constants;

@Path(Constants.INDEX_URL)
@Permissions({ Constants.ROLE_PAGE1, Constants.ROLE_PAGE2, Constants.ROLE_PAGE3 })
public class IndexPage extends LoggedPage {
}
