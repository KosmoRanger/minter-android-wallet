/*
 * Copyright (C) by MinterTeam. 2018
 * @link <a href="https://github.com/MinterTeam">Org Github</a>
 * @link <a href="https://github.com/edwardstock">Maintainer Github</a>
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package network.minter.bipwallet.addresses.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import network.minter.bipwallet.R;
import network.minter.bipwallet.addresses.models.AddressItem;
import network.minter.bipwallet.internal.helpers.ContextHelper;
import network.minter.core.MinterSDK;
import timber.log.Timber;

import static network.minter.bipwallet.internal.helpers.MathHelper.bdHuman;

/**
 * minter-android-wallet. 2018
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
public class AddressListAdapter extends PagedListAdapter<AddressItem, AddressListAdapter.ViewHolder> {
    private final static DiffUtil.ItemCallback<AddressItem> sDiffUtilCallback = new DiffUtil.ItemCallback<AddressItem>() {
        @Override
        public boolean areItemsTheSame(AddressItem oldItem, AddressItem newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(AddressItem oldItem, AddressItem newItem) {
            return oldItem.equals(newItem);
        }
    };
    private LayoutInflater mInflater;
    private OnAddressClickListener mOnAddressClickListener;
    private OnBalanceClickListener mOnBalanceClickListener;
    private OnSetMainListener mOnSetMainListener;

    public AddressListAdapter() {
        super(sDiffUtilCallback);
    }

    private AddressListAdapter(@NonNull AsyncDifferConfig<AddressItem> config) {
        super(config);
    }

    public void setOnSetMainListener(OnSetMainListener listener) {
        mOnSetMainListener = listener;
    }

    public void setOnBalanceClickListener(OnBalanceClickListener listener) {
        mOnBalanceClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mInflater.inflate(R.layout.item_list_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressItem item = getItem(position);

        if (item.isMain || position == 0) {
            holder.addressTitle.setText("Main address");
        } else {
            holder.addressTitle.setText("Address #" + (position + 1));
        }
        holder.defSwitch.setChecked(item.isMain || position == 0);

        holder.address.setText(item.address.toString());
        holder.address.setOnClickListener(v -> {
            if (mOnAddressClickListener != null) {
                mOnAddressClickListener.onClick(v, holder.addressTitle.getText().toString(), item);
            }
        });
        holder.actionCopy.setOnClickListener(v -> {
            ContextHelper.copyToClipboard(v.getContext(), item.address.toString());
        });
        holder.securedValue.setText(item.isServerSecured ? "Bip Wallet" : "You");
        holder.defSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mOnSetMainListener != null) {
                mOnSetMainListener.onSetMain(isChecked, item);
            }
        });

        if (mOnBalanceClickListener != null) {
            holder.balanceLayout.setOnClickListener(v -> {
                mOnBalanceClickListener.onClick(v, getItem(holder.getAdapterPosition()));
            });
        }

        if (item.balance.getStateLiveData().getValue() == AddressItem.BalanceState.Loading) {
            Timber.d("Showing progress");
            holder.balanceValue.setVisibility(View.INVISIBLE);
            holder.balanceProgress.setVisibility(View.VISIBLE);
            item.balance.getStateLiveData().observeForever(new Observer<AddressItem.BalanceState>() {
                @Override
                public void onChanged(@Nullable AddressItem.BalanceState balanceState) {
                    Timber.d("State is null");
                    if (balanceState == null || balanceState == AddressItem.BalanceState.Loading) {
                        return;
                    }

                    item.balance.getStateLiveData().removeObserver(this);
                    Timber.d("State is loaded");
                    Observable.just(true)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(res -> {
                                notifyItemChanged(holder.getAdapterPosition());
                            });

                }
            });
        } else {
            Timber.d("Showing amount");
            holder.balanceValue.setText(bdHuman(item.balance.getAmount()) + " " + MinterSDK.DEFAULT_COIN);
            holder.balanceValue.setVisibility(View.VISIBLE);
            holder.balanceProgress.setVisibility(View.GONE);
        }
    }

    public void setOnAddressClickListener(OnAddressClickListener clickListener) {
        mOnAddressClickListener = clickListener;
    }

//    private int findByAddress(MinterAddress address) {
//        int pos = 0;
//        for (ProfileAddressData d : mItems) {
//            if (d.address.equals(address)) {
//                return pos;
//            }
//            pos++;
//        }
//
//        return pos;
//    }

    public interface OnAddressClickListener {
        void onClick(View v, String addressName, AddressItem address);
    }

    public interface OnBalanceClickListener {
        void onClick(View v, AddressItem address);
    }

    public interface OnSetMainListener {
        void onSetMain(boolean isMain, AddressItem data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.address_title) TextView addressTitle;
        @BindView(R.id.address) TextView address;
        @BindView(R.id.action_copy) View actionCopy;
        @BindView(R.id.balance_layout) View balanceLayout;
        @BindView(R.id.balance_value) TextView balanceValue;
        @BindView(R.id.balance_progress) ProgressBar balanceProgress;
        @BindView(R.id.secured_value) TextView securedValue;
        @BindView(R.id.default_switch) Switch defSwitch;
        @BindView(R.id.row_address) View rowAddress;
        @BindView(R.id.row_secured) View rowSecured;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}


