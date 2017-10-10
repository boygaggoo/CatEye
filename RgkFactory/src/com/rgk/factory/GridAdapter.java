package com.rgk.factory;
import com.rgk.factory.ControlCenter.ResultHandle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class GridAdapter extends BaseAdapter{
	private static String TAG = "GridAdapter";
	private LayoutInflater mInflater;
	public GridAdapter(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}	
	
	@Override
	public int getCount() {
		return Util.singleTestTitle.size();
	}

	@Override
	public String getItem(int position) {
		return Util.singleTestTitle.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.gridview_item, parent, false);			
		} else {
			view = convertView;
		}
		
		Button gridViewItem = (Button) view.findViewById(R.id.gridview_item);
		gridViewItem.setText(Util.singleTestTitle.get(position));
		gridViewItem.setTextColor(ResultHandle.GetColorFromSystem(Util.singleTestTag.get(position)));
		return view;
	}
}
