package net.anotheria.anoplass.api.activity;


import net.anotheria.anoplass.api.API;
/**
 * This api is used to track users activity.
 *
 * @author lrosenberg
 */
public interface ActivityAPI extends API {
    /**
     * Called by the APIFilter for each url called by the session.
     * Fire {@link net.anotheria.anoplass.api.generic.observation.ObservationAPI#fireSubjectUpdateForCurrentUser(String, String)}
     * sending {@link net.anotheria.anoplass.api.generic.observation.ObservationSubjects#ACTIVITY_UPDATE} subject, in case when user is logged in.
     *
     * @param url resource url itself
     */
    void notifyMyActivity(String url);

}
