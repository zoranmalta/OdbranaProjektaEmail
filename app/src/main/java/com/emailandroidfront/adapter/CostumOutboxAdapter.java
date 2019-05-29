package com.emailandroidfront.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emailandroidfront.R;
import com.emailandroidfront.model.Message;

import java.util.ArrayList;

public class CostumOutboxAdapter extends ArrayAdapter<Message> implements View.OnClickListener{
    private ArrayList<Message> messages;
    private ArrayList<Message> messagesFull;
    Context mContext;

    public CostumOutboxAdapter(ArrayList<Message> messages,Context context){
        super(context, R.layout.row_outbox, messages);
        this.messages = messages;
        messagesFull=new ArrayList<>(messages);
        this.mContext=context;
    }

    // View lookup cache
    //tu cuvamo vrednosti za oredjeni row
    private static class ViewHolder {
        TextView txtFrom;
        TextView txtSubject;
        TextView txtDate;
        ImageView info;
    }

    //postavili smo osluskivac na view i poredimo id kliknutog dela view-a
    //tj na dugme + i ispisujemo poruku bez akcije (snackbar) dobra fora moze trebati
    @Override
    public void onClick(View v) {

//        int position=(Integer) v.getTag();
//        Object object= getItem(position);
//        Message message=(Message) object;
//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Content email: " +message.getContent(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Message message=  getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_outbox, parent, false);
            viewHolder.txtFrom = (TextView) convertView.findViewById(R.id.from_outbox);
            viewHolder.txtSubject = (TextView) convertView.findViewById(R.id.subject_outbox);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date_outbox);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info_outbox);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //animacija za skrolovanje liste ako je veca od ekrana
        // ima dva layouta koji se aktiviraju u zavisnosti kako vucemo po ekranu i menjamo vrednost position
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtFrom.setText("From : "+message.getAccountDto().getUsername());
        viewHolder.txtSubject.setText("Subject : "+message.getSubject());
        viewHolder.txtDate.setText(message.getSendto());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position); //postavlja tag za ikonu na koju mozemo kliknuti
        // Return the completed view to render on screen
        return convertView;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Message> filteredList=new ArrayList<>();
            //ako je uneti text u search prazan ili null ostaje cela lista else filtriramo po tri kriterijuma
            if(constraint==null||constraint.length()==0){
                filteredList.addAll(messagesFull);
            }else{
                String filterPattern=constraint.toString().toLowerCase().trim();
                for(Message m :messagesFull){
                    if(m.getSubject().toLowerCase().contains(filterPattern)||
                            m.getAccountDto().getUsername().toLowerCase().contains(filterPattern)||
                            m.getContent().toLowerCase().contains(filterPattern)){
                        filteredList.add(m);
                    }
                }
            }
            //povratna vrednost je neka mapa i filteredList stavljamo u values
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //result ubacujemo u listu koju saljemo u adapter i notifikujemo promenu
            messages.clear();
            messages.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
}
