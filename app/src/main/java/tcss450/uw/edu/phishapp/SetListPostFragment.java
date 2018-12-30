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
import tcss450.uw.edu.phishapp.blog.SetList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetListPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SetListPostFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public SetListPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_set_list_post, container, false);
        Button b = v.findViewById(R.id.setListPost_fullButton);
        b.setOnClickListener(this);

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        SetList post = (SetList) getArguments().getSerializable(getString(R.string.setList_key));
        TextView longDate = getActivity().findViewById(R.id.setListPost_longDate);
        TextView location = getActivity().findViewById(R.id.setListPost_location);
        TextView setListData = getActivity().findViewById(R.id.setListPost_setListData);
        TextView setListNotes = getActivity().findViewById(R.id.setListPost_setListNotes);

        longDate.setText(post.getLongDate());
        location.setText(post.getLocation());
        setListData.setText(Html.fromHtml(post.getSetListData(), Html.FROM_HTML_MODE_LEGACY));
        setListNotes.setText(Html.fromHtml(post.getSetListNotes(), Html.FROM_HTML_MODE_LEGACY));

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
        SetList post = (SetList) getArguments().getSerializable(getString(R.string.setList_key));
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
        void onFragmentInteraction(Uri uri);
    }
}
