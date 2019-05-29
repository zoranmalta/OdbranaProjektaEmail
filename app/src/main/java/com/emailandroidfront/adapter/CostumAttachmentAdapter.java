package com.emailandroidfront.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emailandroidfront.R;
import com.emailandroidfront.model.Attachment;

import java.util.ArrayList;

public class CostumAttachmentAdapter extends ArrayAdapter<Attachment> implements View.OnClickListener {

    private ArrayList<Attachment> attachments;
    Context mContext;

    public CostumAttachmentAdapter(ArrayList<Attachment> attachments,Context context){
        super(context, R.layout.attachment_row,attachments);
        this.attachments=attachments;
        this.mContext=context;

    }
    // View lookup cache
    //tu cuvamo vrednosti za oredjeni row
    private static class ViewHolder {
        TextView txtType;
        TextView txtName;
    }

    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Attachment attachment = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.attachment_row, parent, false);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type_attachment);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name_attachment);

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

        viewHolder.txtType.setText("Type : "+attachment.getType());
        viewHolder.txtName.setText("Name : "+attachment.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
