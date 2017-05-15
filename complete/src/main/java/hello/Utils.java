package hello;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by veereshkalmath on 30/04/17.
 */
public class Utils {

    static void combinationUtil(List<Set<String>> arr, int n, int r, int index,
                                List<Set<String>> data, int i, Set<String> combinationList)
    {
//        if(combinationList.size() >= 100) {
//            return;
//        }
        // Current combination is ready to be printed, print it
        if (index == r)
        {
            Set<String> combine = new HashSet<String>();
            for (int j=0; j<r; j++){
                if( j == 0) {
                    combine.addAll(data.get(j));
                }else{
                    combine.retainAll(data.get(j));
                }
            }

            //System.out.println("");
            for(String c : combine){
                combinationList.add(c);
            }
            return;
        }

        // When no more elements are there to put in data[]
        if (i >= n)
            return;

        // current is included, put next at next location
//        data[index] = arr.get(i);
        data.add(index, arr.get(i));
        combinationUtil(arr, n, r, index+1, data, i+1, combinationList);

        // current is excluded, replace it with next (Note that
        // i+1 is passed, but index is not changed)
        combinationUtil(arr, n, r, index, data, i+1, combinationList);
    }

    // The main function that prints all combinations of size r
    // in arr[] of size n. This function mainly uses combinationUtil()
    static void generateCombination(List<Set<String>> arr, int n, int r, Set<String> combinationList)
    {
        // A temporary array to store all combination one by one
        List<Set<String>> data = new ArrayList<Set<String>>();


        // Print all combination using temprary array 'data[]'
        combinationUtil(arr, n, r, 0, data, 0, combinationList);
    }

}
