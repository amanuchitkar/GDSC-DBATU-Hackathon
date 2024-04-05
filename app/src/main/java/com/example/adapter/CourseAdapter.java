package com.example.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.psp.Course;
import com.example.psp.R;
import com.example.psp.CourseDetails;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final ArrayList<Course> courses;

    public CourseAdapter(ArrayList<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.titleTextView.setText(course.getCourseTitle());
        holder.descriptionTextView.setText(course.getCourseDescription());

        String semesterText = String.format(holder.itemView.getContext().getString(R.string.semester_dp), course.getCourseSemester());
        holder.semesterTextView.setText(semesterText);

        // Der OnClickListener funktioniert so wie das onClick-Attribut bei den Buttons.
        // Hierbei beziehe ich mich jedoch auf die ImageView innerhalb des Items.
        holder.courseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), CourseDetails.class);
                intent.putExtra("COURSE_OBJECT", course);
                view.getContext().startActivity(intent);

                ((Activity) view.getContext()).overridePendingTransition(R.anim.animation_slide_right_in, R.anim.animation_slide_left_in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView semesterTextView;
        private final ImageView courseDetailsImageView;
        private final CardView courseCardView;

        public CourseViewHolder(View view) {
            super(view);

            titleTextView =  view.findViewById(R.id.itemCourseTitleTextView);
            descriptionTextView = view.findViewById(R.id.itemCourseDescriptionTextView);
            semesterTextView = view.findViewById(R.id.itemCourseSemesterTextView);
            courseDetailsImageView = view.findViewById(R.id.itemCourseDetailsImageView);
            courseCardView = view.findViewById(R.id.itemCourseCardView);
        }
    }
}
