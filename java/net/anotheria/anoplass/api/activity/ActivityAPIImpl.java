package net.anotheria.anoplass.api.activity;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anoplass.api.generic.observation.ObservationAPI;
import net.anotheria.anoplass.api.generic.observation.ObservationSubjects;

/**
 * {@link ActivityAPI} implementation.
 */
public class ActivityAPIImpl extends AbstractAPIImpl implements ActivityAPI {
    /**
     * Last url parameter.
     */
    private static final String LAST_URL = "LAST_URL";
    /**
     * {@link LoginAPI} instance.
     */
    private LoginAPI loginAPI;
    /**
     * {@link ObservationAPI} instance.
     */
    private ObservationAPI observationAPI;

    @Override
    public void init() throws APIInitException {
        loginAPI = APIFinder.findAPI(LoginAPI.class);
        observationAPI = APIFinder.findAPI(ObservationAPI.class);
    }

    //this is just a test impl of a test api sofar.
    //In the future we will use this api to detect users inactivity.

    @Override
    public void notifyMyActivity(String url) {
        setAttributeInSession(LAST_URL, url);
        if(loginAPI.isLogedIn())
            observationAPI.fireSubjectUpdateForCurrentUser(ObservationSubjects.ACTIVITY_UPDATE, this.getClass().getName());

    }

}
