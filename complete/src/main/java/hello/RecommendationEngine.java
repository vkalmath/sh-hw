package hello;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import hello.model.Listen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by veereshkalmath on 30/04/17.
 */
public class RecommendationEngine implements IRecommendation {

    private static final IRecommendation INSTANCE = new RecommendationEngine();

    //Map containing  subtopicId(key) -> Set<userId> (value)
    private final Map<String, Set<String>> mMeditationIdToUsers;

    //Map containing userID(key) to set<subTopicId> (value)
    private final Map<String, Set<String>> mUserIdToMeditaions;

    //Map containg MeditationId to List of recommended Meditations
    private final Map<String, List<String>> mMeditationIdToListOfMeditations;

    final Gson gson = new GsonBuilder().create();

    private RecommendationEngine() {
        mMeditationIdToUsers = new ConcurrentHashMap<String, Set<String>>();
        mUserIdToMeditaions = new ConcurrentHashMap<String, Set<String>>();
        mMeditationIdToListOfMeditations = new ConcurrentHashMap<String, List<String>>();
    }

    public static IRecommendation getInstance(){
        return INSTANCE;
    }

    @Override
    public void buildRecommendations(String logfile) {
        readStream(logfile);
    }

    @Override
    public String getRecommendation(String meditationId) {
        List<String> list = null;
        if(mMeditationIdToListOfMeditations.containsKey(meditationId)) {
            list = mMeditationIdToListOfMeditations.get(meditationId);
        } else {
            list = getRecommendationList(meditationId);
        }

        Type listType = new TypeToken<List<String>>() {}.getType();
        String json = gson.toJson(list, listType);

        return json;
    }


    private void readStream(String fileName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            InputStream inputStream = classLoader.getResourceAsStream(fileName);
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

            // Read file in stream mode
            reader.beginArray();
            while (reader.hasNext()) {
                // Read data into object model
                Listen listen = gson.fromJson(reader, Listen.class);
                System.out.println(listen);

                //Build the meditationIdtouser map
                buildMeditationIdToUserSet(listen);

                //Build userId To MeditationIdSet map
                buildUserIdToMeditationSet(listen);

            }
            reader.close();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates a Map which containing meditationId as Key to the Set of usersId listened to it
     * @param listen
     */
    private void buildMeditationIdToUserSet(Listen listen) {
        final String meditationId = listen.getSubtopic();

        if(mMeditationIdToUsers.containsKey(meditationId)) {
            Set<String> userSet = mMeditationIdToUsers.get(meditationId);
            userSet.add(listen.getUser());
        } else {
            Set<String> userSet = new HashSet<String>();
            userSet.add(listen.getUser());
            mMeditationIdToUsers.put(meditationId, userSet);
        }
    }


    /**
     * Creates a Map containing useId as Key to the Set of MeditationsIds user Listened to
     * @param listen
     */
    private void buildUserIdToMeditationSet(Listen listen) {
        final String userId = listen.getUser();
        final String meditationId = listen.getSubtopic();

        if(mUserIdToMeditaions.containsKey(userId)) {
            Set<String> meditationSet = mUserIdToMeditaions.get(userId);
            meditationSet.add(meditationId);
        } else {
            Set<String> meditationSet = new HashSet<String>();
            meditationSet.add(meditationId);
            mUserIdToMeditaions.put(userId, meditationSet);
        }
    }

    /**
     * Gets the Set of Users for the given MeditationId
     * @param meditationId
     * @return
     */
    private Set<String> getSetOfUsers(String meditationId) {
        if(mMeditationIdToUsers.containsKey(meditationId)) {
            return mMeditationIdToUsers.get(meditationId);
        }
        return new HashSet<String>();
    }


    /**
     * Gets the List of Sets of MeditationIds for give Set of Users
     * @param set
     * @return
     */
    private List<Set<String>> getListOfMeditationSet(Set<String> set) {
        List<Set<String>> list = new ArrayList<Set<String>>();

        for(String userId : set) {
            if(mUserIdToMeditaions.containsKey(userId)){
                if(mUserIdToMeditaions.get(userId).size() > 1) {
                    list.add(mUserIdToMeditaions.get(userId));
                }
            }
        }
        return list;
    }


    /**
     * gets the Recommended List of meditationIds. Return Empty list if none exist
     * @param meditationId
     * @return
     */
    private List<String> getRecommendationList(String meditationId){
        Set<String> userSet = getSetOfUsers(meditationId);
        if(userSet.size() > 0) {
            List<Set<String>> listOfMeditationSet = getListOfMeditationSet(userSet);

            List<String> listOfMeditations = getMergedListOfMeditations(listOfMeditationSet, meditationId);

            return listOfMeditations;
        }

        return new ArrayList<String>();
    }


    /**
     * Gets the list of recommended MeditationId List. This method performs caching, if Recommendation List already Exists
     * it returns the cached List.
     * @param listOfMeditationSet
     * @param meditationId
     * @return
     */
    private List<String> getMergedListOfMeditations(List<Set<String>> listOfMeditationSet, String meditationId) {

        if(mMeditationIdToListOfMeditations.containsKey(meditationId)) {
            return mMeditationIdToListOfMeditations.get(meditationId);
        } else {
            final Set<String> recommendedSet = new HashSet<String>();
            Utils.generateCombination(listOfMeditationSet, listOfMeditationSet.size(), 2, recommendedSet);


            final List<String> list = getTop4Recommendation(recommendedSet, meditationId);
            mMeditationIdToListOfMeditations.put(meditationId, list);
            return list;
        }
    }

    private List<String> getTop4Recommendation(Set<String> recommendedSet, String meditationId) {
        final List<String> list = new ArrayList<String>();
        recommendedSet.remove(meditationId);
        if(recommendedSet.size() > 4){
            int count = 0;
            for(String recommnedation : recommendedSet) {
                if(count == 4 ){
                    break;
                }
                list.add(recommnedation);
                count++;
            }
        }else{
            for(String recommnedation : recommendedSet) {
                list.add(recommnedation);
            }
        }
        return list;
    }


}
