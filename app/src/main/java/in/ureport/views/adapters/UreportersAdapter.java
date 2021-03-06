package in.ureport.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.ureport.R;
import in.ureport.listener.ItemSelectionListener;
import in.ureport.listener.OnCreateIndividualChatListener;
import in.ureport.helpers.ImageLoader;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class UreportersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "UreportersAdapter";

    private List<User> ureportersList;
    private List<User> searchUreportersList;
    private Set<User> selectedUreporters;

    private Boolean selectionEnabled = false;
    private Boolean searchEnabled = false;
    private Integer maxSelectionCount;

    private OnCreateIndividualChatListener onCreateIndividualChatListener;
    private ItemSelectionListener<User> itemSelectionListener;

    public UreportersAdapter(List<User> ureportersList) {
        this.ureportersList = ureportersList;
    }

    public UreportersAdapter() {
        this.ureportersList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_ureporter, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(getCurrentUreportersList().get(position));
    }

    private List<User> getCurrentUreportersList() {
        if(searchEnabled) {
            return searchUreportersList;
        }
        return ureportersList;
    }

    @Override
    public long getItemId(int position) {
        return getCurrentUreportersList().get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return getCurrentUreportersList().size();
    }

    public List<User> getUreportersList() {
        return ureportersList;
    }

    public void update(List<User> ureportersList) {
        this.ureportersList = ureportersList != null ? ureportersList : new ArrayList<User>();
        notifyDataSetChanged();
    }

    public void setOnCreateIndividualChatListener(OnCreateIndividualChatListener onCreateIndividualChatListener) {
        this.onCreateIndividualChatListener = onCreateIndividualChatListener;
    }

    public void setSelectionEnabled(Boolean selectionEnabled, Integer maxSelectionCount, List<User> users) {
        this.maxSelectionCount = maxSelectionCount;
        this.selectionEnabled = selectionEnabled;

        if(users != null)
            this.selectedUreporters = new HashSet<>(users);
        else
            this.selectedUreporters = new HashSet<>();
    }

    public void setSelectionEnabled(Boolean selectionEnabled, Integer maxSelectionCount) {
        setSelectionEnabled(selectionEnabled, maxSelectionCount, null);
    }

    public void setSelectionEnabled(Boolean selectionEnabled) {
        setSelectionEnabled(selectionEnabled, null, null);
    }

    public void setItemSelectionListener(ItemSelectionListener<User> itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
    }

    public Set<User> getSelectedUreporters() {
        return selectedUreporters;
    }

    public void search(String query) {
        searchEnabled = true;
        searchUreportersList = getUreportersByNickname(query);
        notifyDataSetChanged();
    }

    public void clearSearch() {
        searchEnabled = false;
        notifyDataSetChanged();
    }

    @NonNull
    private List<User> getUreportersByNickname(String query) {
        List<User> resultUreportersList = new ArrayList<>();

        List<User> ureportersList = getUreportersList();
        query = query.toLowerCase();
        for (User user : ureportersList) {
            if(user.getNickname().toLowerCase().contains(query)) {
                resultUreportersList.add(user);
            }
        }
        return resultUreportersList;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView picture;
        private final CheckBox selected;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            picture = (ImageView) itemView.findViewById(R.id.picture);
            selected = (CheckBox) itemView.findViewById(R.id.selected);
            itemView.setOnClickListener(selectionEnabled ? null : onItemClickListener);
            selected.setOnCheckedChangeListener(selectionEnabled ? onUserCheckedListener : null);
            selected.setOnClickListener(selectionEnabled ? onUserSelectedListener : null);
        }

        public void bindView(User user) {
            name.setText(user.getNickname());
            ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());

            selected.setVisibility(selectionEnabled ? View.VISIBLE : View.GONE);
            if(selectionEnabled) selected.setChecked(selectedUreporters.contains(user));
        }

        private View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onCreateIndividualChatListener != null)
                    onCreateIndividualChatListener.onCreateIndividualChat(getCurrentUreportersList().get(getLayoutPosition()));
            }
        };

        private View.OnClickListener onUserSelectedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemSelectionListener == null) return;

                User user = getCurrentUreportersList().get(getLayoutPosition());
                if(selected.isChecked()) {
                    itemSelectionListener.onItemSelected(user);
                } else {
                    itemSelectionListener.onItemDeselected(user);
                }
            }
        };

        private CompoundButton.OnCheckedChangeListener onUserCheckedListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                User user = getCurrentUreportersList().get(getLayoutPosition());
                if(isChecked) {
                    if(maxSelectionCount == null || selectedUreporters.size() <= maxSelectionCount)
                        selectedUreporters.add(user);
                    else
                        showMaximumNumberLimitError();
                } else {
                    selectedUreporters.remove(user);
                }
            }
        };

        private void showMaximumNumberLimitError() {
            Toast.makeText(itemView.getContext()
                    , itemView.getContext().getString(R.string.ureporters_selected_maximum, maxSelectionCount)
                    , Toast.LENGTH_LONG).show();
        }
    }
}
