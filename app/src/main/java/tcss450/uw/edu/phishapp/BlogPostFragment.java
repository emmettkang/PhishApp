package tcss450.uw.edu.phishapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tcss450.uw.edu.phishapp.blog.BlogPost;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlogPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BlogPostFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    public BlogPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_blog_post, container, false);
    Button b = v.findViewById(R.id.full_button);
        b.setOnClickListener(this);

        return v;
}

    @Override
    public void onStart() {
        super.onStart();

        BlogPost post = (BlogPost) getArguments().getSerializable(getString(R.string.blog_key));
        TextView title = getActivity().findViewById(R.id.blog_title);
        TextView date = getActivity().findViewById(R.id.pub_date);
        TextView teaser = getActivity().findViewById(R.id.teaser_blog);

        title.setText(post.getTitle());
        date.setText(post.getPubDate());
        teaser.setText(Html.fromHtml(post.getTeaser(), Html.FROM_HTML_MODE_LEGACY));

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        BlogPost post = (BlogPost) getArguments().getSerializable(getString(R.string.blog_key));
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(post.getUrl()));
        startActivity(i);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
    }
}
