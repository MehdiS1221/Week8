package algonquin.cst2335.inclassexamples_s21;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MessageDetailsFragment extends Fragment {
    MessageListFragment.ChatMessage chosenMessage;
    int chosenPosition;

    public MessageDetailsFragment(MessageListFragment.ChatMessage message, int position){
        chosenMessage = message;
        chosenPosition = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsView = inflater.inflate(R.layout.details_layout, container, false);
        TextView messageView = detailsView.findViewById(R.id.messageV);
        TextView sendView = detailsView.findViewById(R.id.sendV);
        TextView timeView = detailsView.findViewById(R.id.timeV);
        TextView idView = detailsView.findViewById(R.id.databaseV);

        messageView.setText("Message is: " + chosenMessage.getMessage());
        sendView.setText("Send or Receive? " + chosenMessage.getSendOrReceive());
        timeView.setText("Time send: " + chosenMessage.getTimeSent());
        idView.setText("Database Id is: " + chosenMessage.getId());

        Button closeButton = detailsView.findViewById(R.id.close);
        closeButton.setOnClickListener( closeClicker ->{
            getParentFragmentManager().beginTransaction().remove(this).commit();

        });

        Button deleteButton = detailsView.findViewById(R.id.delete);
        deleteButton.setOnClickListener( closeClicker ->{
            ChatRoom parentActivity = (ChatRoom)getContext();
            parentActivity.notifyMessageDeleted(chosenMessage, chosenPosition);

            getParentFragmentManager().beginTransaction().remove(this).commit();
        });




        return detailsView;
    }
}
