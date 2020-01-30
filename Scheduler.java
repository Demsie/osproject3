
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * class Scheduler implements FCFS, RR, SPN, SRT, HRRN, FB scheduling
 * algorithms. The program used data from the file "jobs.txt" in format:
 *
 * Process Arrival time Service (Burst) Time 
 * A	0	3 
 * B	2	6 
 * C	4	4
 *
 * Usage: java Scheduler [one of scheduling algorithms or ALL]
 */
public class Scheduler {
    
    /**
    * Class Job used to store data of job
    * in Scheduler class
    */
    private static class Job implements Comparable {
    public Job(String name, int arrivalTime, int serviceTime){
            this.name = name;
            this.arrivalTime = arrivalTime;
            this.serviceTime = serviceTime;
            priority = 0;
        }
    /**
     * Override method compareTo
     * @param other object of type Job
     * @return 
     */
    @Override
    public int compareTo(Object o){
        Job other = (Job) o;
        if(priority ==  other.priority){
            return name.compareTo(other.name);
        }
        if(priority < other.priority)
            return -1;
        
        return 1;
    }
 
    public String getName(){
        return name;
    }
    public int getArrivalTime(){
        return arrivalTime;
    }
    public int getServiceTime(){
        return serviceTime;
    }
    public void setPriority(int priority){
        this.priority = priority;
    }
    public void setServiceTime(int newTime){
        this.serviceTime = newTime;
    }
    private String name;
    private int arrivalTime;
    private int serviceTime;
    private int priority;       
    
}
    
 /**
 * Class JobDone used to store data of finished job
 * in Scheduler class
 */
public static class JobDone {
    public JobDone(String name, int startTime, int endTime){
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    private String name;
    private int startTime;
    private int endTime;
    
    String getName(){
        return name;
    }
    
    int getStartTime(){
        return startTime;
    }
    
    int getEndTime(){
        return endTime;
    }         
}

    /**
     * FCFS scheduling algorithm
     *
     * @param jobs ArrayList with jobs
     * @return ArrayList with finished jobs
     */
    public static ArrayList<JobDone> FCFS(ArrayList<Job> jobs) {
        // create array list for finished jobs
        ArrayList<JobDone> jobDone = new ArrayList<>();
        // set current time
        int currentTime = 0;
        // iterate jobs
        for (int i = 0; i < jobs.size(); i++) {
            if (currentTime < jobs.get(i).getArrivalTime()) {
                // update current time
                currentTime = jobs.get(i).getArrivalTime();
            }
            // add job to jobDone
            JobDone jd = new JobDone(jobs.get(i).getName(), currentTime,
                    currentTime + jobs.get(i).getServiceTime());
            jobDone.add(jd);
            // update current time
            currentTime += jobs.get(i).getServiceTime();
        }
        return jobDone;
    }

    /**
     * RR scheduling algorithm
     *
     * @param jobs ArrayList with jobs
     * @return ArrayList with finished jobs
     */
    public static ArrayList<JobDone> RR(ArrayList<Job> jobs) {
        // create array list for finished jobs
        ArrayList<JobDone> jobDone = new ArrayList<>();
        // create queue for jobs
        ArrayDeque<Job> q = new ArrayDeque<>();
        // set quantum
        int quantum = 1;
        // set current time
        int currentTime = 0;
        // iterate jobs
        for (int i = 0; i < jobs.size(); i++) {
            while (!q.isEmpty()) {
                // peek job from the queue 
                Job j = q.peek();
                if (currentTime < jobs.get(i).getArrivalTime()) {
                    q.pop(); // pop job from queue
                    // add job to jobDone
                    jobDone.add(new JobDone(j.getName(), currentTime,
                            currentTime + quantum));
                    // calculate remaining time
                    int remainingTime = j.getServiceTime() - quantum;
                    if (remainingTime > 0) {
                        // add job to the queue
                        q.add(new Job(j.getName(), j.getArrivalTime(), remainingTime));
                    }
                    currentTime += quantum;
                } else {
                    break;
                }
            }
            if (currentTime < jobs.get(i).getArrivalTime()) {
                currentTime = jobs.get(i).getArrivalTime();
            }
            // add job to jobDone
            JobDone jd = new JobDone(jobs.get(i).getName(), currentTime,
                    currentTime + quantum);
            jobDone.add(jd);
            // calculate remaining time
            int remainingTime = jobs.get(i).getServiceTime() - quantum;
            if (remainingTime > 0) {
                // add job to the queue
                q.add(new Job(jobs.get(i).getName(), jobs.get(i).getArrivalTime(),
                        remainingTime));
            }
            // update current time
            currentTime += quantum;
        }
        while (!q.isEmpty()) {
            // pop job from the queue
            Job j = q.pop();
            // add job to jobDone
            jobDone.add(new JobDone(j.getName(), currentTime,
                    currentTime + quantum));
            int remainingTime = j.getServiceTime() - quantum;
            if (remainingTime > 0) {
                // add job to the queue
                q.add(new Job(j.getName(), j.getArrivalTime(), remainingTime));
            }
            currentTime += quantum;
        }
        return jobDone;
    }

    /**
     * SPN scheduling algorithm
     *
     * @param jobs ArrayList with jobs
     * @return ArrayList with finished jobs
     */
    public static ArrayList<JobDone> SPN(ArrayList<Job> jobs) {
        // create array list for finished jobs
        ArrayList<JobDone> jobDone = new ArrayList<>();
        PriorityQueue<Job> q = new PriorityQueue<>();
        // set current time
        int currentTime = 0;
        // iterate jobs
        for (int i = 0; i < jobs.size(); i++) {
            while (!q.isEmpty()) {
                // peek job 
                Job j = q.peek();
                // poll the job only if there are no arrived jobs
                if (currentTime < jobs.get(i).getArrivalTime()) {
                    q.poll();
                    // add job to jobDone
                    jobDone.add(new JobDone(j.getName(), currentTime,
                            currentTime + j.getServiceTime()));
                    currentTime += j.getServiceTime();
                } else {
                    break;
                }
            }
            if (currentTime <= jobs.get(i).getArrivalTime()) {
                currentTime = jobs.get(i).getArrivalTime();
                // add job to jobDone
                JobDone jd = new JobDone(jobs.get(i).getName(), currentTime,
                        currentTime + jobs.get(i).getServiceTime());
                jobDone.add(jd);
                currentTime += jobs.get(i).getServiceTime();
            } else {
                Job j = new Job(jobs.get(i).getName(), jobs.get(i).getArrivalTime(),
                        jobs.get(i).getServiceTime());
                j.setPriority(jobs.get(i).getServiceTime());
                // add job to the queue
                q.add(j);
            }
        }

        while (!q.isEmpty()) {
            Job j = q.poll();
            // add job to jobDone
            jobDone.add(new JobDone(j.getName(), currentTime,
                    currentTime + j.getServiceTime()));
            currentTime += j.getServiceTime();
        }
        return jobDone;
    }

    /**
     * SRT scheduling algorithm
     *
     * @param jobs ArrayList with jobs
     * @return ArrayList with finished jobs
     */
    public static ArrayList<JobDone> SRT(ArrayList<Job> jobs) {
        // create array list for finished jobs
        ArrayList<JobDone> jobDone = new ArrayList<>();
        PriorityQueue<Job> q = new PriorityQueue<>();
        // set current time
        int currentTime = 0;
        int endTime = 0;
        int startTime = 0;
        Job runningJob = null;
        int i = 0;
        while (!q.isEmpty() || i < jobs.size()) {
            if (runningJob != null) {
                // iterate the time till endtime of running job
                while (i < jobs.size()
                        && jobs.get(i).getArrivalTime() < endTime) {
                    // increase current time
                    if (currentTime < jobs.get(i).getArrivalTime()) {
                        currentTime = jobs.get(i).getArrivalTime();
                    }
                    // add all jobs with current arrival time
                    while (i < jobs.size()
                            && jobs.get(i).getArrivalTime() == currentTime) {
                        // create the job from i-th job in ArrajList jobs
                        Job j = new Job(jobs.get(i).getName(),
                                jobs.get(i).getArrivalTime(),
                                jobs.get(i).getServiceTime());
                        // set priority as remaining service time
                        j.setPriority(jobs.get(i).getServiceTime());
                        // add the job to priority queue
                        q.add(j);
                        // increase the index
                        i++;
                    }
                    if (!q.isEmpty()) {
                        Job j = q.peek();
                        // check whether new comming job has shorter service time
                        if (j.getServiceTime() < runningJob.getServiceTime()) {
                            // stop running job
                            q.poll();
                            jobDone.add(new JobDone(runningJob.getName(),
                                    startTime, currentTime));
                            // create a new job with remaining service time
                            Job newJob = new Job(runningJob.getName(),
                                    runningJob.getArrivalTime(),
                                    runningJob.getServiceTime()
                                    - currentTime + startTime);
                            newJob.setPriority(newJob.getServiceTime());
                            // add new job to priority queue
                            q.add(newJob);
                            // start the new comming job
                            runningJob = j;
                            startTime = currentTime;
                            endTime = currentTime + j.getServiceTime();
                        }
                    }
                }
                // no jobs with higher priority than running job
                // finish the running job
                jobDone.add(new JobDone(runningJob.getName(), startTime,
                        endTime));
                currentTime = endTime;
                runningJob = null;
            } else {
                // add all jobs with  current arrival time
                while (i < jobs.size()
                        && jobs.get(i).getArrivalTime() == currentTime) {
                    // create the job from i-th job in ArrajList jobs
                    Job j = new Job(jobs.get(i).getName(),
                            jobs.get(i).getArrivalTime(),
                            jobs.get(i).getServiceTime());
                    j.setPriority(jobs.get(i).getServiceTime());
                    // add the job to priority queue
                    q.add(j);
                    // increase index
                    i++;
                }
                if (!q.isEmpty()) {
                    // get job from priority queue
                    runningJob = q.poll();
                    startTime = currentTime;
                    endTime = currentTime + runningJob.getServiceTime();
                }
            }
        }
        if (runningJob != null) {
            // add the last running job
            jobDone.add(new JobDone(runningJob.getName(),
                    startTime, endTime));
        }
        return jobDone;
    }

    /**
     * HRRN scheduling algorithm
     *
     * @param jobs ArrayList with jobs
     * @return ArrayList with finished jobs
     */
    public static ArrayList<JobDone> HRRN(ArrayList<Job> jobs) {
        // create array list for finished jobs
        ArrayList<JobDone> jobDone = new ArrayList<>();
        // set current time
        int currentTime = 0;
        // indicate if the job is completed
        boolean[] completed = new boolean[jobs.size()];
        for (int i = 0; i < jobs.size(); i++) {
            completed[i] = false;
        }
        // number of completed jobs
        int countCompleted = 0;
        // while all jobs completed
        while (countCompleted < jobs.size()) {
            //set max ratio as minimum value
            double maxRatio = -9999999.0;
            // set maxRatioIndex as impossible index
            int maxRatioIndex = -1;
            // iterate jobs in reverse order (A takes precedence over B)
            for (int i = jobs.size() - 1; i >= 0; i--) {
                // if completed and arrived
                if (!completed[i]
                        && currentTime >= jobs.get(i).getArrivalTime()) {
                    // calculate current ratio
                    double currentRatio = ((currentTime
                            - jobs.get(i).getArrivalTime()
                            + jobs.get(i).getServiceTime())
                            / (double) jobs.get(i).getServiceTime());
                    // find the maximum ratio
                    if (maxRatio < currentRatio) {
                        maxRatio = currentRatio;
                        maxRatioIndex = i;
                    }
                }
            }
            if (maxRatioIndex != -1) { // found job with max ratio
                JobDone jd = new JobDone(jobs.get(maxRatioIndex).getName(),
                        currentTime,
                        currentTime + jobs.get(maxRatioIndex).getServiceTime());
                // update current time
                currentTime += jobs.get(maxRatioIndex).getServiceTime();
                // add completed job
                jobDone.add(jd);
                // mark as completed
                completed[maxRatioIndex] = true;
                // increase counter
                countCompleted++;
            } else { // no arrived jobs
                for (int i = 0; i < jobs.size(); i++) {
                    if (!completed[i]) {
                        // increase the current time
                        currentTime = jobs.get(i).getArrivalTime();
                        break;
                    }
                }
            }
        }
        return jobDone;
    }

    /**
     * FB scheduling algorithm
     *
     * @param jobs ArrayList with jobs
     * @return ArrayList with finished jobs
     */
    public static ArrayList<JobDone> FB(ArrayList<Job> jobs) {
        // create array list for finished jobs
        ArrayList<JobDone> jobDone = new ArrayList<>();
        // create queues
        ArrayDeque<Job> q1 = new ArrayDeque<>();
        ArrayDeque<Job> q2 = new ArrayDeque<>();
        ArrayDeque<Job> q3 = new ArrayDeque<>();
        int quantum = 1;
        int quantum2 = 1;
        int quantum3 = 1;
        // set current time
        int currentTime = 0;
        for (int i = 0; i < jobs.size(); i++) {
            // add job to the queue1
            q1.add(new Job(jobs.get(i).getName(), jobs.get(i).getArrivalTime(),
                    jobs.get(i).getServiceTime()));
        }

        while (!q1.isEmpty() || !q2.isEmpty() || !q3.isEmpty()) {
            while (!q1.isEmpty() && q1.peek().getArrivalTime() <= currentTime) {
                Job j = q1.pop();
                // add job to jobDone
                jobDone.add(new JobDone(j.getName(), currentTime,
                        currentTime + quantum));
                int remainingTime = j.getServiceTime() - quantum;
                if (remainingTime > 0) {
                    // add job to the queue2
                    q2.add(new Job(j.getName(), j.getArrivalTime(),
                            remainingTime));
                }
                // update current time
                currentTime += quantum;
            }
            if (!q2.isEmpty()) {
                Job j = q2.pop();

                int remainingTime = j.getServiceTime() - quantum2;
                if (remainingTime > 0) {
                    // add job to jobDone
                    jobDone.add(new JobDone(j.getName(), currentTime,
                            currentTime + quantum2));
                    // add job to the queue3
                    q3.add(new Job(j.getName(), j.getArrivalTime(),
                            remainingTime));
                    // update current time
                    currentTime += quantum2;
                }
                if (remainingTime <= 0) {
                    // add job to jobDone
                    jobDone.add(new JobDone(j.getName(), currentTime,
                            currentTime + j.getServiceTime()));
                    // update current time
                    currentTime += j.getServiceTime();
                }
            }
            if (!q3.isEmpty() && q2.isEmpty()
                    && (q1.isEmpty() || q1.peek().getArrivalTime() > currentTime)) {
                Job j = q3.pop();

                int remainingTime = j.getServiceTime() - quantum3;
                if (remainingTime > 0) {
                    // add job to jobDone
                    jobDone.add(new JobDone(j.getName(), currentTime,
                            currentTime + quantum3));
                    // add job to the queue3
                    q3.add(new Job(j.getName(), j.getArrivalTime(),
                            remainingTime));
                    // update current time
                    currentTime += quantum3;
                }
                if (remainingTime <= 0) {
                    // add job to jobDone
                    jobDone.add(new JobDone(j.getName(), currentTime,
                            currentTime + j.getServiceTime()));
                    // update current time
                    currentTime += j.getServiceTime();
                }
            }
            if (q2.isEmpty() && q3.isEmpty() && !q1.isEmpty()
                    && q1.peek().getArrivalTime() > currentTime) {
                // update current time
                currentTime = q1.peek().getArrivalTime();
            }
        }
        return jobDone;
    }

    /**
     * Prints scheduling as graph
     *
     * @param jobDone ArrayList with finished jobs
     * @param n number of jobs
     */
    public static void printGraph(ArrayList<JobDone> jobDone, int n) {
        // print header line
        for (int i = 0; i < n; i++) {
            System.out.print((char) ('A' + i) + " ");
        }
        System.out.println();
        // print graph
        int currentTime = 0;
        // for each job (partional job)
        for (int i = 0; i < jobDone.size(); i++) {
            // calculate horizontal offset
            int offsetH = jobDone.get(i).getName().charAt(0) - 'A';
            // calculate vertical offset
            int offsetV = jobDone.get(i).getStartTime() - currentTime;
            // calculate duration
            int duration = jobDone.get(i).getEndTime()
                    - jobDone.get(i).getStartTime();
            // print vertical offset
            for (int j = 0; j < offsetV; j++) {
                System.out.println();
            }
            // print k lines
            for (int k = 0; k < duration; k++) {
                // print horizontal offset
                for (int j = 0; j < offsetH; j++) {
                    System.out.print("  ");
                }
                // print character X
                System.out.println('X');
            }
            // update current time
            currentTime = jobDone.get(i).getEndTime();
        }
    }
    /**
     * Main function
     * @param args one of scheduling algorithms or ALL
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: Scheduler "
                    + "[one of scheduling algorithms or ALL]");
            return;
        }

        // arraylist to store jobs
        ArrayList<Job> jobs = new ArrayList<>();
        // try to read the file jobs.txt
        try {
            FileReader fr = new FileReader("jobs.txt");
            Scanner sc = new Scanner(fr);
            // read header line
            while (sc.hasNext()) {
                String header = sc.nextLine();
                if (header.length() != 0) {
                    break;
                }
            }
            // read data
            while (sc.hasNext()) {
                String name = sc.next();
                if (name.length() == 0) {
                    break;
                }
                int arrivalTime = sc.nextInt();
                int serviceTime = sc.nextInt();
                // add jobs into array list
                jobs.add(new Job(name, arrivalTime, serviceTime));
            }
            fr.close();

        } catch (IOException ex) {
            System.out.println("Can not open the file jobs.txt");
            return;
        }

        ArrayList<JobDone> jobDone;
        if (args[0].equals("FCFS")) {
            System.out.println("FCFS scheduling algorithm");
            jobDone = FCFS(jobs);
            printGraph(jobDone, jobs.size());
        } else if (args[0].equals("RR")) {
            System.out.println("RR scheduling algorithm");
            jobDone = RR(jobs);
            printGraph(jobDone, jobs.size());
        } else if (args[0].equals("SPN")) {
            System.out.println("SPN scheduling algorithm");
            jobDone = SPN(jobs);
            printGraph(jobDone, jobs.size());
        } else if (args[0].equals("SRT")) {
            System.out.println("SRT scheduling algorithm");
            jobDone = SRT(jobs);
            printGraph(jobDone, jobs.size());
        } else if (args[0].equals("HRRN")) {
            System.out.println("HRRN scheduling algorithm");
            jobDone = HRRN(jobs);
            printGraph(jobDone, jobs.size());
        } else if (args[0].equals("FB")) {
            System.out.println("FB scheduling algorithm");
            jobDone = FB(jobs);
            printGraph(jobDone, jobs.size());
        }else if (args[0].equals("ALL")) {
            System.out.println("FCFS scheduling algorithm");
            jobDone = FCFS(jobs);
            printGraph(jobDone, jobs.size());
            System.out.println("RR scheduling algorithm");
            jobDone = RR(jobs);
            printGraph(jobDone, jobs.size());
            System.out.println("SPN scheduling algorithm");
            jobDone = SPN(jobs);
            printGraph(jobDone, jobs.size());
            System.out.println("SRT scheduling algorithm");
            jobDone = SRT(jobs);
            printGraph(jobDone, jobs.size());
            System.out.println("HRRN scheduling algorithm");
            jobDone = HRRN(jobs);
            printGraph(jobDone, jobs.size());
            System.out.println("FB scheduling algorithm");
            jobDone = FB(jobs);
            printGraph(jobDone, jobs.size());
        }else{
            System.out.println("Unknown scheduling algorithm");
        }


    }
}
