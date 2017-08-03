package practice1.entities;

import practice1.models.VectorSpaceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by romdhane on 26/07/17.
 */
public class Concurrency {
    private EvaluationEntity e;
    private boolean normalised;
    private List<Double> TF =new ArrayList<Double>();
    private List<Double> IDF =new ArrayList<Double>();

    public void setIDF(List<Double> IDF) {
        this.IDF = IDF;
    }

    public void setTF(List<Double> TF) {

        this.TF = TF;
    }

    public List<Double> getIDF() {

        return IDF;
    }

    public List<Double> getTF() {

        return TF;
    }

    public void setNormalised(boolean normalised) {
        this.normalised = normalised;
    }

    public void setE(EvaluationEntity e) {
        this.e = e;

    }

    public List<List<Double>> testThread()
    {
        final List<List<Double>> listResults = new ArrayList<List<Double>>();


        //create a callable for each method

        Callable<Void> callable1 = new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                List<Double> tf = new ArrayList<Double>();
                listResults.add(0,tf);

                VectorSpaceModel vsm = new VectorSpaceModel();
                //setTF(vsm.getTF(e, e.getQuery(), normalised));
                tf = vsm.getTF(e, e.getQuery(), normalised);
                listResults.add(0,tf);
                return null;
            }
        };

        Callable<Void> callable2 = new Callable<Void>()
        {
            @Override
            public Void call() throws Exception

            {
                List<Double> idf = new ArrayList<Double>();
                listResults.add(1,idf);

                VectorSpaceModel vsm = new VectorSpaceModel();

                idf =vsm.getIDF(e);
                System.out.println(idf);

                listResults.add(1,idf);
                return null;
            }
        };


        //add to a list
        List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
        taskList.add(callable1);
        taskList.add(callable2);


        //create a pool executor with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try
        {
            //start the threads
            List<Future<Void>> futureList = executor.invokeAll(taskList);

            for(Future<Void> voidFuture : futureList)
            {
                try
                {
                    //check the status of each future.  get will block until the task
                    //completes or the time expires
                    voidFuture.get(200, TimeUnit.MILLISECONDS);

                }
                catch (ExecutionException e)
                {
                    System.err.println("Error executing task " + e.getMessage());
                }
                catch (TimeoutException e)
                {
                    System.err.println("Timed out executing task" + e.getMessage());
                }

            }


        }
        catch (InterruptedException ie)
        {
            //do something if you care about interruption;
        }
     return  listResults;
    }


}
