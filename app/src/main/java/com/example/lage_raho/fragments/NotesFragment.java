package com.example.lage_raho.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lage_raho.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View myNotesList = inflater.inflate(R.layout.fragment_notes, container, false);

        RecyclerView notes_list = myNotesList.findViewById(R.id.collect_your_notes);
        notes_list.setLayoutManager(new LinearLayoutManager(getContext()));

        return myNotesList;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // create a view holder for custom_notes_layout.xml file
    public static class NotesViewHolder extends RecyclerView.ViewHolder {

        TextView subjectName;
        ImageView pdfIcon;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectName = itemView.findViewById(R.id.pdf_file_name);
            pdfIcon = itemView.findViewById(R.id.Pdf_image);
        }
    }
}
