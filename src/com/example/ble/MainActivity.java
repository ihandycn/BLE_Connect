package com.example.ble;

import java.util.ArrayList;
import java.util.List;

import com.example.ble.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MainActivity extends Activity {
	private static final String TAG = "Keith";

	private Button btn_searchDev;
	private Button btn_Back;
	private ListView lv_bleList;
	private Handler mHandler;
	@SuppressWarnings("unused")
	private boolean mScanning;
	private static final long SCAN_PERIOD = 60000; // scan 60s, and then stop the scan 
	private BluetoothAdapter mBluetoothAdapter;
	private DeviceListAdapter mDevListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mHandler = new Handler();
		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		initViews();
		addListen();
	}

	private void initViews() {
		btn_Back = (Button) findViewById(R.id.btn_Back);
		btn_searchDev = (Button) findViewById(R.id.btn_searchDev);
		lv_bleList = (ListView) findViewById(R.id.lv_bleList);
		mDevListAdapter = new DeviceListAdapter();
		lv_bleList.setAdapter(mDevListAdapter);
	}

	private void addListen() {
		btn_Back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		btn_searchDev.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				scanLeDevice(true);
			}
		});
	}

	/**
	 * @Title: scanLeDevice
	 * @Description: 
	 * @param @param enable
	 * @return void
	 * @throws
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				final byte[] scanRecord) {
			Log.i(TAG, "onLeScan(), device name:" + device.getName());
			Log.i(TAG, "onLeScan(), device address:" + device.getAddress());
			Log.i(TAG, "onLeScan(), device bond state:" + device.getBondState());
			Log.i(TAG, "onLeScan(), device type:" + device.getType());
			Log.i(TAG, "onLeScan(), device name:" + device.getUuids());
			Log.i(TAG, "onLeScan(), device rssi:" + rssi);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mDevListAdapter.addDevice(device);
					mDevListAdapter.notifyDataSetChanged();
					// Check this is Somy device
					if (scanRecord != null && scanRecord.length > 0) {
						if (String.format("%02X",scanRecord[0]).equalsIgnoreCase("11")
								&& String.format("%02X",scanRecord[1]).equalsIgnoreCase("FF")
								&& String.format("%02X",scanRecord[2]).equalsIgnoreCase("02")
								&& String.format("%02X",scanRecord[3]).equalsIgnoreCase("E0")) {
							// Get the temperature value
							int highNum = Integer.parseInt(String.format("%02X",scanRecord[9]) + "00", 16);
							int lowNum = Integer.parseInt(String.format("%02X",scanRecord[10]) + "", 16);
							float temperature = (highNum + lowNum) / 10;
							int part1 = (highNum + lowNum) / 10; // ex. 37.2度, part1 is 37, part2 is 2
							int part2 = (highNum + lowNum) % 100;
							Log.e(TAG, "onLeScan(), temperature:" + temperature);
							Log.e(TAG, "onLeScan(), temperature part1:" + part1 + ", part2:" + part2);
							Toast.makeText(MainActivity.this, "" + temperature + "摄氏度", 500).show();
							
							// 0-电压正常；1-电压低
							int battery = Integer.parseInt(scanRecord[10] + "", 16) >> 7;
							Log.e(TAG, "onLeScan(), battery status:" + battery);
						}
						
						final StringBuilder stringBuilder = new StringBuilder(
								scanRecord.length);
						for (byte byteChar : scanRecord)
							stringBuilder.append(String.format("%02X ",byteChar));
						
						Log.i(TAG, "Scan record:" + stringBuilder);
					}
				}
			});
		}
	};

	class DeviceListAdapter extends BaseAdapter {

		private List<BluetoothDevice> mBleArray;
		private ViewHolder viewHolder;

		public DeviceListAdapter() {
			mBleArray = new ArrayList<BluetoothDevice>();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mBleArray.contains(device)) {
				mBleArray.add(device);
			}
		}

		@Override
		public int getCount() {
			return mBleArray.size();
		}

		@Override
		public BluetoothDevice getItem(int position) {
			return mBleArray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(MainActivity.this)
						.inflate(R.layout.list_device, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_devName = (TextView) convertView
						.findViewById(R.id.tv_devName);
				viewHolder.tv_devAddress = (TextView) convertView
						.findViewById(R.id.tv_devAddress);
				convertView.setTag(viewHolder);
			} else {
				convertView.getTag();
			}

			// add-Parameters
			BluetoothDevice device = mBleArray.get(position);
			String devName = device.getName();
			if (devName != null && devName.length() > 0) {
				viewHolder.tv_devName.setText(devName);
			} else {
				viewHolder.tv_devName.setText("unknow-device");
			}
			viewHolder.tv_devAddress.setText(device.getAddress());

			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_devName, tv_devAddress;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
