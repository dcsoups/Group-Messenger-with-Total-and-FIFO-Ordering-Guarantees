/*

CSE 486/586 Distributed Systems
Programming Assignment 2B
Group Messenger on Android

Name:Mihir Kulkarni
Person Number:50168610
mihirdha@buffalo.edu


*/

package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.Timestamp;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {


    private static final int SERVER_PORT = 10000;
    private static int id=0;
    private static String myPort;
    private static String MULTICASTTAG;
    private static String RECURRINGTTAG="Recurringtag";


    MulticastLibrary multicastLibrary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*

        Uri.Builder uriBuilder = new Uri.Builder();// From PTestOnClickListener
        uriBuilder.authority("edu.buffalo.cse.cse486586.groupmessenger1.provider");
        uriBuilder.scheme("content");
        Uri providerUri=uriBuilder.build();

        Cursor resultCursor = getContentResolver().query(
                providerUri,    // assume we already created a Uri object with our provider URI
                null,                // no need to support the projection parameter
                "keytoread",    // we provide the key directly as the selection parameter
                null,                // no need to support the selectionArgs parameter
                null                 // no need to support the sortOrder parameter
        );



        ContentValues keyValueToInsert = new ContentValues();

        // inserting <”key-to-insert”, “value-to-insert”>
        keyValueToInsert.put("key", "key - to - insert");
        keyValueToInsert.put("value", "value - to - insert");

        Uri newUri = getContentResolver().insert(
                providerUri,    // assume we already created a Uri object with our provider URI
                keyValueToInsert
        );
*/


        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));


        MULTICASTTAG="mihirMulticast "+myPort;


       //
       Log.e("mihir", "Calling servertask");


        multicastLibrary=new MulticastLibrary();
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e("TAG", "Can't create a ServerSocket"+e);
            return;
        }







        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
        final TextView editText=(TextView) findViewById(R.id.editText1);
        Button sendButton= (Button) findViewById(R.id.button4);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = (String) editText.getText().toString() + "\n";
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg);

                editText.setText("");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }



    /***
     * ServerTask is an AsyncTask that should handle incoming messages. It is created by
     * ServerTask.executeOnExecutor() call in SimpleMessengerActivity.
     *
     * Please make sure you understand how AsyncTask works by reading
     * http://developer.android.com/reference/android/os/AsyncTask.html
     *
     * @author stevko
     *
     */
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
        int seqNum=0;
        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            Log.e("mihir", "Inside servertask");

            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */

            //Reference with Dr. Ko's permission: https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html



            String str="";
            while(true) {
                try {
                    str=multicastLibrary.acceptMessage(serverSocket);

                    //Log.d("mihir","Sending str to display"+str);

                    publishProgress(str);
                }catch (SocketTimeoutException e){
                    continue;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            //return null;
        }
        //TODO http://stackoverflow.com/questions/5517641/publishprogress-from-inside-a-function-in-doinbackground


        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            String message;




            //Display
            //String strReceived = strings[0].trim();

                message=strings[0].trim();

            TextView tv = (TextView) findViewById(R.id.textView1);
            tv.setMovementMethod(new ScrollingMovementMethod());
            tv.append(message + ":" + seqNum + "\t\n");






            seqNum++;
            /*
             * The following code creates a file in the AVD's internal storage and stores a file.
             *
             * For more information on file I/O on Android, please take a look at
             * http://developer.android.com/training/basics/data-storage/files.html
             */

            String filename = "SimpleMessengerOutput";
            String string = message + "\n";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                Log.e("TAG", "File write failed");
            }

            return;
        }
    }




    public class MulticastLibrary{

        int failureHandlingFlag=0;
        int s=-1;
        int seqNum=-1;
        Timer timer;
        int T=1000;
        int ALIVE_AVDS=5;
        String deadAVD=null;
        HashMap<String,Long> heartbeatMap=new HashMap<String, Long>();
        public MulticastLibrary(){
           try {
               //new SuggestionsCheckTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
               timer= new Timer();
               timer.schedule(new TimerTask() {
                   @Override
                   public void run() {
                       //Log.d(MULTICASTTAG,"TIMER");
                       BMulticast(jsonCreate("heartbeat", "", myPort, "", "", ""));
                   }
               },0,T);

           }
           catch (Exception e){
               Log.e(MULTICASTTAG,"Exception in SuggestionCheckTask"+e);
           }
        }
        class QueueElement{
            String msgid,sender,suggester,s,status,message;
            Long timestamp;
            private void logAll(){
                if(timestamp!=null)
                    Log.d(MULTICASTTAG, "QueueElement: msgid:" + msgid + " sender:" + sender + " suggester:" + suggester + " s:" + s + " status:" + status + " message:" + message+" timestamp"+timestamp);
                else
                    Log.d(MULTICASTTAG, "QueueElement: msgid:" + msgid + " sender:" + sender + " suggester:" + suggester + " s:" + s + " status:" + status + " message:" + message+" timestamp is null");
            }
        }
        QueueElementComparator queueElementComparator=new QueueElementComparator();
        PriorityQueue<QueueElement> priorityQueue= new PriorityQueue<QueueElement>(25,queueElementComparator);
        class QueueElementComparator implements Comparator<QueueElement>{

            @Override
            public int compare(QueueElement lhs, QueueElement rhs) {

                if(Integer.parseInt(lhs.s) < Integer.parseInt(rhs.s))   return -1;
                else if(Integer.parseInt(lhs.s) > Integer.parseInt(rhs.s)) return 1;

                if(lhs.status.equals("undeliverable")&&rhs.status.equals("deliverable"))    return -1;
                else if(lhs.status.equals("deliverable")&&rhs.status.equals("undeliverable"))   return 1;

                if(Integer.parseInt(lhs.suggester)<Integer.parseInt(rhs.suggester)) return -1;
                else if (Integer.parseInt(lhs.suggester)>Integer.parseInt(rhs.suggester))   return 1;

                Log.e(MULTICASTTAG,"Two elements in queue are equal. This should NEVER happen");
                return 0;
            }
        }

        class TimestampElement{
            Long timestamp;
            String message;
        }
        HashMap<String,TimestampElement> timestampMap=new HashMap<String, TimestampElement>();
        HashMap<String,HashMap<String,String>> hashMap= new HashMap<String,HashMap<String,String>>();
        class HashMapComparator implements Comparator<Map.Entry<String,String>>{
            @Override
            public int compare(Map.Entry<String, String> lhs, Map.Entry<String, String> rhs) {
                if(Integer.parseInt(lhs.getValue())<Integer.parseInt(rhs.getValue()))   return -1;
                else if(Integer.parseInt(lhs.getValue())>Integer.parseInt(rhs.getValue()))  return 1;

                if(Integer.parseInt(lhs.getKey())<Integer.parseInt(rhs.getKey()))   return -1;
                else if (Integer.parseInt(lhs.getKey())<Integer.parseInt(rhs.getKey()))    return 1;

                Log.e(MULTICASTTAG,"Two elements in map are equal. This should NEVER happen");
                return 0;
            }
        }

        public class SuggestionsCheckTask extends AsyncTask {

            @Override
            protected Void doInBackground(Object[] params) {
                while(true) {
                    Log.d(MULTICASTTAG,"Checking map again");
                    /*for (Map.Entry<String, HashMap<String, String>> e : hashMap.entrySet()) {
                        HashMap<String, String> suggestions = (HashMap<String, String>) e.getValue();
                        if (suggestions.size() == 4 && (System.currentTimeMillis() - timestampMap.get(e.getKey()).timestamp) > 500) {//TODO Assuming only 1 AVD will fail
                            Log.d(MULTICASTTAG, "Time expired. Someone must have failed: msgid:" + e.getKey());
                            //TODO http://stackoverflow.com/questions/922528/how-to-sort-map-values-by-key-in-java
                            SortedSet<String> keys = new TreeSet<String>(suggestions.keySet());
                            int maxS = -1;

                            String maxSuggester = null;
                            for (String key : keys) {
                                String value = suggestions.get(key);
                                // do something
                                if (Integer.parseInt(value) > maxS) {
                                    maxS = Integer.parseInt(value);
                                    maxSuggester = key;
                                }
                            }
                            String message = timestampMap.get(e.getKey()).message;
                            Log.d(MULTICASTTAG, "Sending BMulticast type:" + "finalmessage" + " msgid:" + e.getKey() + " sender:" + myPort + " maxSuggester:" + maxSuggester + " maxS:" + maxS + " message:" + message);
                            BMulticast(jsonCreate("finalmessage", (String) e.getKey(), myPort, Integer.toString(maxS), maxSuggester, message));


                        }
                    }*/
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        private Void checkMap(){
            Log.d(MULTICASTTAG,"Checking map again");
            List<Map.Entry<String, HashMap<String, String>>> removeList = new LinkedList<Map.Entry<String, HashMap<String, String>>>();
                    for (Map.Entry<String, HashMap<String, String>> e : hashMap.entrySet()) {
                        HashMap<String, String> suggestions = (HashMap<String, String>) e.getValue();
                        if (suggestions.size() <= 4 && (System.currentTimeMillis() - timestampMap.get(e.getKey()).timestamp) > 5000) {//TODO Assuming only 1 AVD will fail
                            Log.d(MULTICASTTAG, "Time expired. Someone must have failed: msgid:" + e.getKey());
                            //TODO http://stackoverflow.com/questions/922528/how-to-sort-map-values-by-key-in-java
                            SortedSet<String> keys = new TreeSet<String>(suggestions.keySet());
                            int maxS = -1;

                            String maxSuggester = null;
                            for (String key : keys) {
                                String value = suggestions.get(key);
                                if (Integer.parseInt(value) > maxS) {
                                    maxS = Integer.parseInt(value);
                                    maxSuggester = key;
                                }
                            }
                            String message = timestampMap.get(e.getKey()).message;
                            Log.d(MULTICASTTAG, "Sending BMulticast type:" + "finalmessage" + " msgid:" + e.getKey() + " sender:" + myPort + " maxSuggester:" + maxSuggester + " maxS:" + maxS + " message:" + message);
                            BMulticast(jsonCreate("finalmessage", (String) e.getKey(), myPort, Integer.toString(maxS), maxSuggester, message));
                            removeList.add(e);
                        }
                    }

                    if(removeList!=null) {
                        for (Map.Entry<String, HashMap<String, String>> e : removeList) {
                            Log.d(MULTICASTTAG,"Removed from map "+e.getKey());
                            hashMap.remove(e.getKey());
                        }
                    }

                    List<QueueElement> queueRemoveList= new LinkedList<QueueElement>();
                    for(QueueElement q: priorityQueue){
                        if(q.status.equals("undeliverable")&&(System.currentTimeMillis() - q.timestamp) > 8000){
                            queueRemoveList.add(q);
                        }
                    }
                    if(queueRemoveList!=null) {
                        for (QueueElement q : queueRemoveList) {
                            Log.d(MULTICASTTAG, "Removed from queue " + q.msgid);
                            priorityQueue.remove(q);
                        }
                    }


                    tryDelivery();


            return null;
        }
        private String acceptMessage(ServerSocket serverSocket) throws IOException {
            String jsonMessageStr;
            //serverSocket.setSoTimeout(2000);
           // Log.e(RECURRINGTTAG, "before accept");
            Socket s = serverSocket.accept();
            //Log.e(RECURRINGTTAG, "Data incoming");

            InputStream ip = s.getInputStream();
            InputStreamReader isr = new InputStreamReader(ip);
            BufferedReader br=new BufferedReader(isr);
            jsonMessageStr=br.readLine();
            //Log.d(RECURRINGTTAG, "Json message received:" + jsonMessageStr);

/*


            OutputStream op;
            PrintWriter pw;
            op = s.getOutputStream();
            pw = new PrintWriter(op, true);
            pw.println("Ack");
            pw.flush();
            Log.e("mihir2", "Sending Acknowledgement Message ");
            op.flush();
            op.close();

*/

            br.close();
            isr.close();
            ip.close();
            s.close();
            //TODO Write algo
            try {
                return multicastLibrary.processMessage(jsonMessageStr);
            }catch(NullPointerException e){
                return null;
            }

        }
        private void processFirstMessage(String type,String msgid, String sender, String message) throws IOException, JSONException {
            Log.d(MULTICASTTAG,"In processFirstMessage type:"+type+" msgid:"+msgid+" sender:"+sender+" Global(before incrementing) s:"+s+" message:"+message);
            s++;
            Log.d(MULTICASTTAG, "Sending proposal to " + sender + " type:" + "proposal" + " msgid:" + msgid + " sender:" + myPort + " s:" + s + " message:" + message);
            unicast(jsonCreate("proposal", msgid, myPort, Integer.toString(s), myPort, message), sender);

            //TODO add to queue
            QueueElement queueElement=new QueueElement();
            queueElement.msgid=msgid;
            queueElement.s=Integer.toString(s) ;
            queueElement.message=message;
            queueElement.sender=sender;
            queueElement.suggester=myPort;
            queueElement.status="undeliverable";
            queueElement.timestamp=System.currentTimeMillis();

            priorityQueue.add(queueElement);

            Log.d(MULTICASTTAG, "Element added in queue. Queue state is...");
            for(QueueElement q:priorityQueue) q.logAll();




        }
        private void processProposal(String type,String msgid, String sender,String suggester,String receivedS, String message){

            Log.d(MULTICASTTAG,"In processProposal type:"+type+" msgid:"+msgid+" sender:"+sender+" suggester:"+suggester+" receivedS:"+receivedS+" message:"+message);


            HashMap<String,String> suggestions;

            if(hashMap.containsKey(msgid)){
                suggestions=hashMap.get(msgid);
            }
            else{
                Log.d(MULTICASTTAG,"It is first proposal for the message type:"+type+" msgid:"+msgid+" sender:"+sender+"Global(before incrementing) s:"+s+" message:"+message);
                suggestions=new HashMap<String, String>();
            }
            //TODO susceptible to duplicate message?
            suggestions.put(suggester, receivedS);

            hashMap.put(msgid, suggestions);

            checkSuggestions(msgid, message);
/*


            if(suggestions.size()>=ALIVE_AVDS){
                //TODO http://stackoverflow.com/questions/922528/how-to-sort-map-values-by-key-in-java
                Log.d(MULTICASTTAG,"Alive AVDs are "+ALIVE_AVDS);
                hashMap.remove(msgid);
                SortedSet<String> keys = new TreeSet<String>(suggestions.keySet());
                int maxS=-1;

                String maxSuggester=null;
                for (String key : keys) {
                    String value = suggestions.get(key);
                    if(Integer.parseInt(value)>maxS){
                        maxS=Integer.parseInt(value);
                        maxSuggester=key;
                    }
                }
                Log.d(MULTICASTTAG,"Sending BMulticast type:"+"finalmessage"+" msgid:"+msgid+" sender:"+myPort+" maxSuggester:"+maxSuggester+" maxS:"+maxS+" message:"+message);
                BMulticast(jsonCreate("finalmessage",msgid,myPort,Integer.toString(maxS),maxSuggester,message));




            }



*/





        }
        private void checkSuggestions(String msgid,String message){
            HashMap<String,String> suggestions;

            if(hashMap.containsKey(msgid)){
                suggestions=hashMap.get(msgid);
            }
            else{
                Log.e(MULTICASTTAG,"msgId does not exist in hashmap");
                return;
            }
            int subtract=0;
            if(deadAVD!=null){
                if(suggestions.keySet().contains(deadAVD)){
                    subtract=1;
                }
            }
            if(suggestions.size()-subtract==ALIVE_AVDS){
                //TODO http://stackoverflow.com/questions/922528/how-to-sort-map-values-by-key-in-java
                Log.d(MULTICASTTAG,"Alive AVDs are "+ALIVE_AVDS);
                hashMap.remove(msgid);
                SortedSet<String> keys = new TreeSet<String>(suggestions.keySet());
                int maxS=-1;

                String maxSuggester=null;
                for (String key : keys) {
                    String value = suggestions.get(key);
                    if(Integer.parseInt(value)>maxS){
                        maxS=Integer.parseInt(value);
                        maxSuggester=key;
                    }
                }
                Log.d(MULTICASTTAG,"Sending BMulticast type:"+"finalmessage"+" msgid:"+msgid+" sender:"+myPort+" maxSuggester:"+maxSuggester+" maxS:"+maxS+" message:"+message);
                BMulticast(jsonCreate("finalmessage",msgid,myPort,Integer.toString(maxS),maxSuggester,message));




            }
        }
        private void processFinalMessage(String type,String msgid, String sender,String suggester,String receivedS, String message){

            Log.d(MULTICASTTAG,"In processFinalMessage type:"+type+" msgid:"+msgid+" sender:"+sender+" suggester:"+suggester+" receivedS:"+receivedS+" message:"+message);
            if(s<Integer.parseInt(receivedS)) s=Integer.parseInt(receivedS);
            Iterator<QueueElement>it=priorityQueue.iterator();
            for(QueueElement q:priorityQueue){
                if(q.msgid.equals(msgid)){
                    QueueElement queueElement=q;
                    priorityQueue.remove(q);
                    queueElement.msgid=msgid;
                    queueElement.s = receivedS;
                    //queueElement.message=message;
                    queueElement.sender=sender;
                    queueElement.suggester=suggester;
                    queueElement.status="deliverable";

                    priorityQueue.add(queueElement);

                    Log.d(MULTICASTTAG, "Queue modified. Queue state is...");

                    break;
                }

            }

            for(QueueElement q:priorityQueue) q.logAll();




            tryDelivery();



            //Collections.sort(priorityQueue,new QueueElementComparator);



        }
        private void tryDelivery(){
            QueueElement queueElement;
            while(priorityQueue.size()>0 && (priorityQueue.peek().status.equals("deliverable") || priorityQueue.peek().sender.equals(deadAVD))){

                //Build URI
                Uri.Builder uriBuilder = new Uri.Builder();// From PTestOnClickListener
                uriBuilder.authority("edu.buffalo.cse.cse486586.groupmessenger2.provider");
                uriBuilder.scheme("content");
                Uri providerUri=uriBuilder.build();

                queueElement=priorityQueue.poll();
                if(queueElement.sender.equals(deadAVD)) continue;
                //Insert in content provider
                ContentValues keyValueToInsert = new ContentValues();

                // inserting <”key-to-insert”, “value-to-insert”>
                seqNum++;
                Log.e(MULTICASTTAG, "storing message- Key:"+seqNum+"   Message:"+queueElement.message);
                keyValueToInsert.put("key", seqNum);
                keyValueToInsert.put("value", queueElement.message);

                Uri newUri = getContentResolver().insert(
                        providerUri,    // assume we already created a Uri object with our provider URI
                        keyValueToInsert
                );
            }
        }
        private String processMessage(String str){
                JSONObject jsonObject;
                String type,message,msgid,sender,suggester,receivedS;



            try {
                jsonObject= new JSONObject(str);
                type=jsonObject.getString("type");
                message=jsonObject.getString("message");
                message=message.trim();
                msgid=jsonObject.getString("id");
                sender=jsonObject.getString("sender");
                suggester=jsonObject.getString("suggester");
                receivedS=jsonObject.getString("s");

                processHeartBeat(sender);

                if(failureHandlingFlag==1){
                    operationValkyrie(deadAVD);
                    failureHandlingFlag=2;
                }
                //Log.d(RECURRINGTTAG, "Multicast received type:" + type + " msgid:" + msgid + " sender:" + sender + " suggester:" + suggester + " s:" + s + " message:" + message);
                if(type.equals("firstmessage")){
                    processFirstMessage(type,msgid,sender,message);
                }else if(type.equals("proposal")){
                    processProposal(type, msgid, sender,suggester,receivedS, message);
                }else if(type.equals("finalmessage")){
                    //BMulticast(jsonCreate(type,msgid,sender,receivedS,suggester,message));
                    processFinalMessage(type, msgid, sender,suggester,receivedS, message);
                }

                //checkMap();//TODO This shouldnt be here
                return message;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void processHeartBeat(String sender){
            Long timestamp=System.currentTimeMillis();
            if(!heartbeatMap.containsKey(sender)){
                heartbeatMap.put(sender,timestamp);
            }else if(timestamp-heartbeatMap.get(sender)<6*T){
                heartbeatMap.put(sender,timestamp);
            }
            else{
                Log.e(MULTICASTTAG,"Resurrection of "+sender);
                heartbeatMap.put(sender,timestamp);
            }
            String keyToDelete=null;
            for(Map.Entry<String, Long> entry:heartbeatMap.entrySet()){
                if(failureHandlingFlag==0 && !entry.getKey().equals(myPort)&&timestamp-entry.getValue()>6*T){
                    Log.e(MULTICASTTAG,"AVD "+entry.getKey()+" dead!");
                    keyToDelete=entry.getKey();
                    deadAVD=entry.getKey();
                    failureHandlingFlag=1;
                    //operationValkyrie(entry.getKey());
                    break;
                }
            }
            if(keyToDelete!=null) {
                heartbeatMap.remove(keyToDelete);
                Log.d(MULTICASTTAG,"key "+keyToDelete+" removed");

            }
        }
        private void operationValkyrie(String dead){
            Log.d(MULTICASTTAG,"Executing Valkyrie");
            ALIVE_AVDS=4;

            List<String> removeList=new LinkedList<String>();
            for(Map.Entry<String,HashMap<String,String>> entry:hashMap.entrySet()){
//                checkSuggestions(entry.getKey(), "");


                String msgid=entry.getKey();
                String message="";
                HashMap<String,String> suggestions;

                if(hashMap.containsKey(msgid)){
                    suggestions=hashMap.get(msgid);
                }
                else{
                    Log.e(MULTICASTTAG,"msgId does not exist in hashmap");
                    return;
                }
                int subtract=0;
                if(deadAVD!=null){
                    if(suggestions.keySet().contains(deadAVD)){
                        subtract=1;
                    }
                }
                if(suggestions.size()-subtract==ALIVE_AVDS){
                    //TODO http://stackoverflow.com/questions/922528/how-to-sort-map-values-by-key-in-java
                    Log.d(MULTICASTTAG,"Alive AVDs are "+ALIVE_AVDS);
                    //hashMap.remove(msgid);
                    removeList.add(msgid);
                    SortedSet<String> keys = new TreeSet<String>(suggestions.keySet());
                    int maxS=-1;

                    String maxSuggester=null;
                    for (String key : keys) {
                        String value = suggestions.get(key);
                        if(Integer.parseInt(value)>maxS){
                            maxS=Integer.parseInt(value);
                            maxSuggester=key;
                        }
                    }
                    Log.d(MULTICASTTAG,"Sending BMulticast type:"+"finalmessage"+" msgid:"+msgid+" sender:"+myPort+" maxSuggester:"+maxSuggester+" maxS:"+maxS+" message:"+message);
                    BMulticast(jsonCreate("finalmessage",msgid,myPort,Integer.toString(maxS),maxSuggester,message));
                }


            }
/*
            if(removeList!=null && !removeList.isEmpty()) {
                for (String Id : removeList) {
                    hashMap.remove(Id);
                }
            }
*/

            for(QueueElement q:priorityQueue){
                if(q.sender.equals(deadAVD)){
                    priorityQueue.remove(q);
                    Log.d(MULTICASTTAG, "Queue modified. Queue state is...");
                    //break;
                }

            }
            tryDelivery();

        }
        private void unicast(String msgToSend,String port) {
            //  Log.e(MULTICASTTAG,"AVD fail detected. Failed AVD "+port);

            try {

                OutputStream op;
                PrintWriter pw;
                Socket socket=new Socket();
                //socket.setSoTimeout(2000);
                InetAddress inetAddress=InetAddress.getByAddress(new byte[]{10, 0, 2, 2});
                //Log.d(RECURRINGTTAG,"Is reachable? "+inetAddress.isReachable(2000));
                socket.connect(new InetSocketAddress("10.0.2.2",Integer.valueOf(port)));
                //Log.d(RECURRINGTTAG,"Is connected "+socket.isConnected());
                //socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),Integer.valueOf(port));
                //socket.setSoTimeout(1000);
                //Reference with Dr. Ko's permission: https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
                op = socket.getOutputStream();
                pw = new PrintWriter(op, true);
                pw.println(msgToSend);
                //Log.e(RECURRINGTTAG, "Sending Unicast Message " + msgToSend);
/*


                socket.setSoTimeout(5000);
                InputStream ip=socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(ip);
                BufferedReader br=new BufferedReader(isr);
                String ack=br.readLine();
                Log.d(MULTICASTTAG,"Acknowledgement received");

                br.close();
                isr.close();
                ip.close();

*/

                pw.flush();
                op.flush();
                op.close();

                socket.close();
            }catch(ConnectException ce){
                Log.e(MULTICASTTAG,"AVD fail detected. Failed AVD "+port);
                ce.printStackTrace();
            } catch(SocketTimeoutException e) {
                Log.e(MULTICASTTAG, "AVD Socket failed "+port+" Error:"+e);
            } catch (IOException e){
                Log.e(MULTICASTTAG,"AVD fail detected. Failed AVD "+port);
                e.printStackTrace();
            }
            /*catch(StreamCorruptedException e) {
                Log.e(MULTICASTTAG, "Socket failed "+port+" Error:"+e);
            }catch(EOFException e) {
                Log.e(MULTICASTTAG, "Socket failed "+port+" Error:"+e);
            }catch (IOException e) {
                Log.e("TAG", "ClientTask socket IOException port:"+port+" Error:"+e);
            }
*/

        }
        private void BMulticast(String... msgs){

               // Log.e("mihir", "Inside Clienttask");
                /*String remotePort = REMOTE_PORT0;
                if (msgs[1].equals(REMOTE_PORT0))
                    remotePort = REMOTE_PORT1;
                */
                String msgToSend= msgs[0];

                unicast(msgToSend,"11108");
                unicast(msgToSend,"11112");
                unicast(msgToSend,"11116");
                unicast(msgToSend,"11120");
                unicast(msgToSend, "11124");

               //Log.e(RECURRINGTTAG, "Clienttask ends");

        }

        private void multicastFirstMessage(String msg){

                //TODO https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&ved=0ahUKEwj39_KD3JbLAhWEMhoKHbNtBSwQFggdMAA&url=http%3A%2F%2Fpages.cpsc.ucalgary.ca%2F~verwaal%2Fcourses%me2F559%2Fsupplemental%2Fweek8_day2%2FISISAlg.doc&usg=AFQjCNGf4TRWwDqGgOvpzpQYYWbxBCaI4Q&sig2=nsMhhjc-nRFpzJAAFuZlmA&cad=rja
                Long timestamp=System.currentTimeMillis();
                TimestampElement tse=new  TimestampElement();
                tse.timestamp=timestamp;
                tse.message=msg;
                timestampMap.put(Integer.toString(id) + "_" + myPort,tse);
                BMulticast(jsonCreate("firstmessage", Integer.toString(id) + "_" + myPort, myPort, "", "", msg));
                id++;

            }
        private String jsonCreate(String type,String id,String sender,String s,String suggester,String message){
            try {
                JSONObject jsonObject= new JSONObject();
                jsonObject.put("type",type);
                jsonObject.put("message",message);
                jsonObject.put("sender",sender);
                jsonObject.put("id",id);
                jsonObject.put("s",s);
                jsonObject.put("suggester",suggester);

            return jsonObject.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }




    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

            Log.d("mihir","In ClientTask");
            multicastLibrary.multicastFirstMessage(msgs[0]);

            return null;
        }
    }
}
