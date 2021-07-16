package algonquin.cst2335.inclassexamples_s21;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessageListFragment extends Fragment {

    ChatMessage removedMessage;
    ChatAdapter adt;
    SQLiteDatabase db;
    ArrayList<ChatMessage> messages = new ArrayList<>();//hold our typed messages
    Button send;
    View chatLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        chatLayout = inflater.inflate(R.layout.chatlayout, container, false);




        MyOpenHelper opener = new MyOpenHelper(getContext());
        db = opener.getWritableDatabase();














        Cursor results = db.rawQuery("Select * from " + MyOpenHelper.TABLE_NAME + ";", null);

        int _idCol = results.getColumnIndex("_id");
        int messageCol = results.getColumnIndex(MyOpenHelper.col_message);
        int sendCol = results.getColumnIndex(MyOpenHelper.col_send_receive);
        int timeCol = results.getColumnIndex(MyOpenHelper.col_time_sent);

        while(results.moveToNext()) {
            long id = results.getInt(_idCol);
            String message = results.getString(messageCol);
            String time = results.getString(timeCol);
            int sendOrReceive = results.getInt(sendCol);
            messages.add(new ChatMessage(message, sendOrReceive, time, id));
        }




        EditText messageTyped = chatLayout.findViewById(R.id.messageEdit);
        send = chatLayout.findViewById(R.id.sendbutton);
        RecyclerView chatList = chatLayout.findViewById(R.id.myrecycler);
        Button returns = chatLayout.findViewById(R.id.receive);


        //add an adapter object to the RecyclerView
        adt = new ChatAdapter();

        //     build the list:
        chatList.setAdapter(adt); //need onCreateView, onBindView, getCount
        //   StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        chatList.setLayoutManager(layoutManager);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());


        send.setOnClickListener(click -> {
                    ChatMessage nextMessage = new ChatMessage(messageTyped.getText().toString(), 1, currentDateandTime);

                    ContentValues newRow = new ContentValues();
                    newRow.put(MyOpenHelper.col_message, nextMessage.getMessage());
                    newRow.put(MyOpenHelper.col_send_receive, nextMessage.getSendOrReceive());
                    newRow.put(MyOpenHelper.col_time_sent, nextMessage.getTimeSent());
                    long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
                    nextMessage.setId(newId);

                    messages.add(nextMessage);//adds to array list
                    //clear the edittext:
                    messageTyped.setText("");
                    //refresh the list:
                    adt.notifyItemInserted(messages.size() - 1); //just insert the new row:
                }
        );
        returns.setOnClickListener(click -> {
                    ChatMessage nextMessage = new ChatMessage(messageTyped.getText().toString(), 0 , currentDateandTime);

                    ContentValues newRow = new ContentValues();
                    newRow.put(MyOpenHelper.col_message, nextMessage.getMessage());
                    newRow.put(MyOpenHelper.col_send_receive, nextMessage.getSendOrReceive());
                    newRow.put(MyOpenHelper.col_time_sent, nextMessage.getTimeSent());
                    long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
                    nextMessage.setId(newId);

                    messages.add(nextMessage);//adds to array list
                    //clear the edittext:
                    messageTyped.setText("");
                    //refresh the list:
                    adt.notifyItemInserted(messages.size() - 1); //just insert the new row:
                }
        );













        return chatLayout;
    }

    public void notifyMessageDeleted(ChatMessage chosenMessage, int chosenPosition) {

                removedMessage = messages.get(chosenPosition);
//                messages.remove(chosenPosition);
//                adt.notifyItemRemoved(chosenPosition);


                db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[] { Long.toString( removedMessage.getId() )});


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to delete this?")
                        .setTitle("Title").setPositiveButton("Yes", (dlg, clic) -> {

                    removedMessage = messages.get(chosenPosition);
                    messages.remove(chosenPosition);
                    adt.notifyItemRemoved(chosenPosition);


                    db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[] { Long.toString( removedMessage.getId() )});


//
//                    messages.remove(row);//which message do you delete?
//                    //update list view:
//                    adt.notifyItemRemoved(row);
                    Snackbar.make(chatLayout, "You deleted message #" + chosenPosition, Snackbar.LENGTH_LONG)
                            .setAction("undo", clk ->{
                                db.execSQL("Insert into " + MyOpenHelper.TABLE_NAME + " values('" + removedMessage.getId() +
                                        "','" + removedMessage.getMessage() +
                                        "','" + removedMessage.getSendOrReceive() +
                                        "','" + removedMessage.getTimeSent()+ "');");

                            }).show();
                })
                        .setNegativeButton("No", (dlg, clic) -> {
                        })
                        .create()
                        .show();


    }


    class ChatMessage {  //Data model for a message in a row
        public String message;
        public int sendOrReceive;
        public String timeSent;
        long id;

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        //        public ChatMessage(String s) {
//            message = s;
//        }
        public ChatMessage(String message, int sendOrReceive, String timeSent, long id){
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;
            setId(id);
        }
        public ChatMessage(String message, int sendOrReceive, String timeSent) {
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;


        }

        public long getId() {
            return id;
        }

        public void setId(long l){ id = l;}

        public String getMessage() {
            return message;
        }

        public int getSendOrReceive() {
            return sendOrReceive;
        }

        public String getTimeSent() {
            return currentDateandTime;
        }
    }



    class MyRowViews extends RecyclerView.ViewHolder {
        //this should the Widgets on a row , only have a TextView for message
        TextView rowMessage;
        TextView timeText;
        ChatAdapter adt;


        public MyRowViews(View itemView) { //itemView is a ConstraintLayout, that has <TextView> as sub-item
            super(itemView);

            itemView.setOnClickListener(clik ->
            {
                ChatRoom parentActivity = (ChatRoom)getContext();
                int row = getAbsoluteAdapterPosition();
                parentActivity.userClickedMessage(messages.get(row), row);

//                ChatMessage removedMessage = messages.get(row);
//                messages.remove(row);
//                adt.notifyItemRemoved(row);
//
//
//                db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[] { Long.toString( removedMessage.getId() )});

//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setMessage("Do you want to delete this?")
//                        .setTitle("Title").setPositiveButton("Yes", (dlg, clic) -> {
//
//                    removedMessage = messages.get(row);
//                    messages.remove(row);
//                    adt.notifyItemRemoved(row);
//
//
//                    db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[] { Long.toString( removedMessage.getId() )});
//
//
////
////                    messages.remove(row);//which message do you delete?
////                    //update list view:
////                    adt.notifyItemRemoved(row);
//                    Snackbar.make(rowMessage, "You deleted message #" + row, Snackbar.LENGTH_LONG).show();
//                })
//                        .setNegativeButton("No", (dlg, clic) -> {
//                        })
//                        .create()
//                        .show();


            });


            rowMessage = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);
        }
    }











    class ChatAdapter extends RecyclerView.Adapter {

        @Override
        public int getItemViewType(int position) {
            ChatMessage thisRow = messages.get(position);
            //if (thisRow.getSendOrReceive() == 0) {
            //sent message

            //}
            //return super.getItemViewType(position);
            return thisRow.getSendOrReceive();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            LayoutInflater inflater = getLayoutInflater(); //LayoutInflater is for loading XML layouts
            int layoutID;
            if (viewType == 1)
                layoutID = R.layout.sent_message;
            else
                layoutID = R.layout.receive_message;
            View constraintLayout = inflater.inflate(layoutID, parent, false);//parent is for how much room does it have?

            return new MyRowViews(constraintLayout); //will initialize the TextView
        }

        @Override               //says ViewHolder, but it's acually MyRowViews object
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) { //position is which row we're building

            MyRowViews thisRowLayout = (MyRowViews) holder;
            thisRowLayout.rowMessage.setText(messages.get(position).getMessage());//sets the text on the row
            thisRowLayout.timeText.setText(messages.get(position).getTimeSent());
            //set the date text:
        }

        @Override
        public int getItemCount() {
            return messages.size(); //how many items to show?
        }
    }









}