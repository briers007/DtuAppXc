package com.minorfish.dtuapp.module.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.database.OnDatabaseListener;
import com.minorfish.dtuapp.module.ActFrame2;
import com.minorfish.dtuapp.module.model.EnvironmentHistoryBean;
import com.minorfish.dtuapp.module.model.FangsheHistoryBean;
import com.minorfish.dtuapp.module.model.RealDataHistoryBean;
import com.minorfish.dtuapp.module.model.SensorSettingBean;
import com.minorfish.dtuapp.module.rv.RvHistoryEnvironment;
import com.minorfish.dtuapp.module.rv.RvHistoryFangshe;
import com.minorfish.dtuapp.module.rv.RvHistoryFive;
import com.minorfish.dtuapp.module.widget.ListDialogAdapter;
import com.minorfish.dtuapp.module.widget.ListSensorAdapter;
import com.minorfish.dtuapp.util.Utils;
import com.tangjd.common.abs.BaseFragment;
import com.tangjd.common.utils.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class FmtHistoryMultiYanshi extends BaseFragment {

    private ActFrame2 mActivity;
    //private ActFrameSingle mActivity;
    @Bind(R.id.swim_head)
    LinearLayout swimHead;
    @Bind(R.id.cod_head)
    LinearLayout codHead;
    @Bind(R.id.urea_head)
    LinearLayout ureaHead;

    @Bind(R.id.rv_history_data)
    RvHistoryFive rvHistory;
    @Bind(R.id.rv_history_urea)
    RvHistoryFangshe rvHistoryUrea;
    @Bind(R.id.rv_history_env)
    RvHistoryEnvironment rvHistoryEnv;

    @Bind(R.id.sensor_layout)
    RelativeLayout sensorLayout;
    @Bind(R.id.sensor_text)
    TextView sensorText;

    @Bind(R.id.start_layout)
    RelativeLayout startLayout;
    @Bind(R.id.end_layout)
    RelativeLayout endLayout;
    @Bind(R.id.his_date_start)
    TextView tvDateStart;
    @Bind(R.id.his_date_end)
    TextView tvDateEnd;
    @Bind(R.id.history_total)
    TextView historyTotal;
    @Bind(R.id.history_pre)
    TextView preBtn;
    @Bind(R.id.history_next)
    TextView nextBtn;
    @Bind(R.id.history_current_layout)
    LinearLayout currentLayout;
    @Bind(R.id.history_current_num)
    TextView currentText;

    private int currentSensor = 0;
    private int pageSize = 6;
    private int currentPage = 0;
    private int totalSize = 0;
    private int totalPage = 0;
    long start;
    long end;
    String startStr;
    String endStr;
    private DatePickerDialog startDialog = null;
    private DatePickerDialog endDialog = null;

    private OnDatabaseListener delegate;

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fmt_history_multi_layout_yanshi, container, false);
    }

    @Override
    protected void initView() {
        mActivity = (ActFrame2) getActivity();
        delegate = (OnDatabaseListener) getActivity();

        setSensor();
        mDialogAdapter = new ListDialogAdapter();
        mListSensorAdapter = new ListSensorAdapter();
        long time = System.currentTimeMillis();
        Date date= new Date();
        start = Utils.getTimesMorning(date);
        end = Utils.getTimesNight(date);

        startStr = DateUtil.simpleFormat("yyyy-MM-dd", time);
        endStr = DateUtil.simpleFormat("yyyy-MM-dd", time);
        tvDateStart.setText(startStr);
        tvDateEnd.setText(endStr);
        Calendar now = Calendar.getInstance();
        startDialog = new DatePickerDialog(mActivity, onDateSetStart, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        endDialog = new DatePickerDialog(mActivity, onDateSetEnd, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

        startLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog.show();
            }
        });
        endLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDialog.show();
            }
        });

        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(currentPage == 0){
                   mActivity.showToast("当前已是第一页");
               }else{
                   currentPage--;
                   getPageData();
               }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((currentPage+1)*pageSize - totalSize >= 0){
                    mActivity.showToast("当前已是最后一页");
                }else{
                    currentPage++;
                    getPageData();
                }
            }
        });

        currentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPageDialog();
            }
        });

        sensorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSensorDialog();
            }
        });
    }

    @Override
    protected void getDataJustOnce() {
        currentPage = 0;
        if(currentSensor == 1){
            getEnvData();
        } else if(currentSensor == 2){
            getUreaData();
        } else {
            getData();
        }
    }

    List<SensorSettingBean> mSensorSettingList;
    public void setSensor() {
        mSensorSettingList = mActivity.mSensorSettingList;
        if(mSensorSettingList!=null && mSensorSettingList.size()!=0) {
            SensorSettingBean bean = mSensorSettingList.get(0);
            sensorText.setText(bean.mSensorName);
        }else{
            sensorText.setText("未选择");
        }
    }

    private ListSensorAdapter mListSensorAdapter;

    private void showSensorDialog() {
        if (mSensorSettingList != null) {
            mListSensorAdapter.setList(mSensorSettingList);
            mListSensorAdapter.notifyDataSetChanged();
            View view = View.inflate(mActivity, R.layout.list_dialog, null);
            ListView lv = (ListView) view.findViewById(R.id.dialog_lv);
            lv.setAdapter(mListSensorAdapter);
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            final AlertDialog dialog = builder.setView(view)
                    .show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        SensorSettingBean bean = mSensorSettingList.get(position);
                        if(bean.mSubTypeBeans!=null && bean.mSubTypeBeans.size()>5){ //环境
                            rvHistoryUrea.setVisibility(View.GONE);
                            rvHistoryEnv.setVisibility(View.VISIBLE);
                            rvHistory.setVisibility(View.GONE);
                            swimHead.setVisibility(View.GONE);
                            codHead.setVisibility(View.GONE);
                            ureaHead.setVisibility(View.VISIBLE); //环境
                            currentSensor = 1;
                            currentPage = 0;
                            getEnvData();
                        }else if(bean.mSubTypeBeans!=null && bean.mSubTypeBeans.size()==1){ //放射
                            rvHistoryUrea.setVisibility(View.VISIBLE);
                            rvHistoryEnv.setVisibility(View.GONE);
                            rvHistory.setVisibility(View.GONE);
                            swimHead.setVisibility(View.GONE);
                            codHead.setVisibility(View.VISIBLE);
                            ureaHead.setVisibility(View.GONE); //环境
                            currentSensor = 2;
                            currentPage = 0;
                            getUreaData();
                        }else{
                            rvHistoryUrea.setVisibility(View.GONE);
                            rvHistory.setVisibility(View.VISIBLE);
                            rvHistoryEnv.setVisibility(View.GONE);
                            swimHead.setVisibility(View.VISIBLE);
                            ureaHead.setVisibility(View.GONE);
                            codHead.setVisibility(View.GONE);
                            currentSensor = 0;
                            currentPage = 0;
                            getData();
                        }
                        sensorText.setText(bean.mSensorName);
                    } catch (Exception e) {
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    private void getData() {
        mActivity.showProgressDialog();
        try {

            RuntimeExceptionDao<RealDataHistoryBean, Long> dao = delegate.getDatabaseHelper().getRealDataHistoryDao();
            QueryBuilder qb = dao.queryBuilder();
            qb.setCountOf(true);
            PreparedQuery<RealDataHistoryBean> pq = qb.where().between("date", start, end).prepare();
            long itemsNum = dao.countOf(pq);
            totalSize = (int)itemsNum;
            historyTotal.setText("共 "+itemsNum+" 条记录");
            float totalPageDb = (float)totalSize/pageSize;
            totalPage = (int)Math.ceil(totalPageDb);

            QueryBuilder qbPage = dao.queryBuilder().offset(pageSize*currentPage).limit(pageSize);
            PreparedQuery<RealDataHistoryBean> pqPage = qbPage.where().between("date", start, end).prepare();
            List<RealDataHistoryBean> itemsPage = dao.query(pqPage);
            rvHistory.setData(itemsPage);
            currentText.setText(currentPage+1+"");
        } catch (Exception e) {
            mActivity.dismissProgressDialog();
        }
        mActivity.dismissProgressDialog();
    }

    private void getUreaData() {
        mActivity.showProgressDialog();
        try {
            RuntimeExceptionDao<FangsheHistoryBean, Long> dao = delegate.getDatabaseHelper().getFangsheDataHistoryDao();
            QueryBuilder qb = dao.queryBuilder();
            qb.setCountOf(true);
            PreparedQuery<FangsheHistoryBean> pq = qb.where().between("date", start, end).prepare();
            long itemsNum = dao.countOf(pq);
            totalSize = (int)itemsNum;
            historyTotal.setText("共 "+itemsNum+" 条记录");
            float totalPageDb = (float)totalSize/pageSize;
            totalPage = (int)Math.ceil(totalPageDb);

            QueryBuilder qbPage = dao.queryBuilder().offset(pageSize*currentPage).limit(pageSize);
            PreparedQuery<FangsheHistoryBean> pqPage = qbPage.where().between("date", start, end).prepare();
            final List<FangsheHistoryBean> itemsPage = dao.query(pqPage);
            if (mActivity!=null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvHistoryUrea.setData(itemsPage);
                        currentText.setText(currentPage + 1 + "");
                    }
                });
            }
        } catch (Exception e) {
            mActivity.dismissProgressDialog();
        }
        mActivity.dismissProgressDialog();
    }

    private void getEnvData() {
        mActivity.showProgressDialog();
        try {
            RuntimeExceptionDao<EnvironmentHistoryBean, Long> dao = delegate.getDatabaseHelper().getEnvironmentHistoryDao();
            QueryBuilder qb = dao.queryBuilder();
            qb.setCountOf(true);
            PreparedQuery<EnvironmentHistoryBean> pq = qb.where().between("date", start, end).prepare();
            long itemsNum = dao.countOf(pq);
            totalSize = (int)itemsNum;
            historyTotal.setText("共 "+itemsNum+" 条记录");
            float totalPageDb = (float)totalSize/pageSize;
            totalPage = (int)Math.ceil(totalPageDb);

            QueryBuilder qbPage = dao.queryBuilder().offset(pageSize*currentPage).limit(pageSize);
            PreparedQuery<EnvironmentHistoryBean> pqPage = qbPage.where().between("date", start, end).prepare();
            final List<EnvironmentHistoryBean> itemsPage = dao.query(pqPage);
            if (mActivity!=null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvHistoryEnv.setData(itemsPage);
                        currentText.setText(currentPage + 1 + "");
                    }
                });
            }
        } catch (Exception e) {
            mActivity.dismissProgressDialog();
        }
        mActivity.dismissProgressDialog();
    }

    private void getPageData() {
        mActivity.showProgressDialog();
        try {
            if(currentSensor == 0) {
                RuntimeExceptionDao<RealDataHistoryBean, Long> dao = delegate.getDatabaseHelper().getRealDataHistoryDao();
                QueryBuilder qbPage = dao.queryBuilder().offset(pageSize * currentPage).limit(pageSize);
                PreparedQuery<RealDataHistoryBean> pqPage = qbPage.where().between("date", start, end).prepare();
                List<RealDataHistoryBean> itemsPage = dao.query(pqPage);
                rvHistory.setData(itemsPage);
                currentText.setText(currentPage + 1 + "");
            }else if(currentSensor == 1){//环境
                RuntimeExceptionDao<EnvironmentHistoryBean, Long> dao = delegate.getDatabaseHelper().getEnvironmentHistoryDao();
                QueryBuilder qbPage = dao.queryBuilder().offset(pageSize * currentPage).limit(pageSize);
                PreparedQuery<EnvironmentHistoryBean> pqPage = qbPage.where().between("date", start, end).prepare();
                List<EnvironmentHistoryBean> itemsPage = dao.query(pqPage);
                rvHistoryEnv.setData(itemsPage);
                currentText.setText(currentPage + 1 + "");
            }else{
                RuntimeExceptionDao<FangsheHistoryBean, Long> dao = delegate.getDatabaseHelper().getFangsheDataHistoryDao();
                QueryBuilder qbPage = dao.queryBuilder().offset(pageSize*currentPage).limit(pageSize);
                PreparedQuery<FangsheHistoryBean> pqPage = qbPage.where().between("date", start, end).prepare();
                List<FangsheHistoryBean> itemsPage = dao.query(pqPage);
                rvHistoryUrea.setData(itemsPage);
                currentText.setText(currentPage+1+"");
            }
        } catch (Exception e) {
            mActivity.dismissProgressDialog();
        }
        mActivity.dismissProgressDialog();
    }

    private ListDialogAdapter mDialogAdapter;

    private void showPageDialog() {
        if (totalPage != 0) {
            mDialogAdapter.setList(totalPage);
            mDialogAdapter.notifyDataSetChanged();
            View view = View.inflate(mActivity, R.layout.list_dialog, null);
            ListView lv = (ListView) view.findViewById(R.id.dialog_lv);
            lv.setAdapter(mDialogAdapter);
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            final AlertDialog dialog = builder.setView(view)
                    .show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                       currentPage = position;
                       getPageData();
                    } catch (Exception e) {
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    DatePickerDialog.OnDateSetListener onDateSetStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {
                String tempdate = (Integer.toString(year) + "-" + changedate(Integer.toString(monthOfYear + 1))) + "-" + changedate(Integer.toString(dayOfMonth));
                startStr = tempdate;
                tvDateStart.setText(startStr);
                Date date = Utils.parse(startStr,"yyyy-MM-dd");
                start = Utils.getTimesMorning(date);
                getDataJustOnce();
            } catch (Exception e) {

            }
        }
    };

    DatePickerDialog.OnDateSetListener onDateSetEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {
                String tempdate = (Integer.toString(year) + "-" + changedate(Integer.toString(monthOfYear + 1))) + "-" + changedate(Integer.toString(dayOfMonth));
                if (Utils.isTheDayBefore(tempdate,startStr)) {
                    mActivity.showToast("结束时间不能早于开始时间!");
                    return;
                }
                endStr = tempdate;
                tvDateEnd.setText(endStr);
                Date date = Utils.parse(endStr,"yyyy-MM-dd");
                end = Utils.getTimesNight(date);
                getDataJustOnce();
            } catch (Exception e) {

            }
        }
    };

    public String changedate(String date) {
        if (date != null && date.length() == 1) {
            date = "0" + date;
        }
        return date;
    }
}
