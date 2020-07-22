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
import com.minorfish.dtuapp.module.rv.RvHistoryChangzhou;
import com.minorfish.dtuapp.module.model.ChangzhouDataHistoryBean;
import com.minorfish.dtuapp.module.widget.ListDialogAdapter;
import com.minorfish.dtuapp.util.Utils;
import com.tangjd.common.abs.BaseFragment;
import com.tangjd.common.utils.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class FmtHistoryChangzhou extends BaseFragment {

    private ActFrame2 mActivity;

    @Bind(R.id.rv_history_data)
    RvHistoryChangzhou rvHistory;

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
        return inflater.inflate(R.layout.fmt_history_changzhou_layout, container, false);
    }

    @Override
    protected void initView() {
        mActivity = (ActFrame2) getActivity();
        //mActivity = (ActFrameSingle) getActivity();
        delegate = (OnDatabaseListener) getActivity();

        mDialogAdapter = new ListDialogAdapter();
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
    }

    @Override
    protected void getDataJustOnce() {
        currentPage = 0;
        getData();
    }

    private void getData() {
        mActivity.showProgressDialog();
        try {
//            dao = getHelper().getUsuarioDao();
//            QueryBuilder queryBuilder = dao.queryBuilder();
//            queryBuilder.setCountOf(true);
//            queryBuilder.setWhere(queryBuilder.where().eq("name", "Joe Smith" ));
//            Long usuarios = dao.countOf(queryBuilder.prepare());

            RuntimeExceptionDao<ChangzhouDataHistoryBean, Long> dao = delegate.getDatabaseHelper().getChangzhouDataHistoryDao();
            QueryBuilder qb = dao.queryBuilder();
            qb.setCountOf(true);
            PreparedQuery<ChangzhouDataHistoryBean> pq = qb.where().between("date", start, end).prepare();
            long itemsNum = dao.countOf(pq);
            totalSize = (int)itemsNum;
            historyTotal.setText("共 "+itemsNum+" 条记录");
            float totalPageDb = (float)totalSize/pageSize;
            totalPage = (int)Math.ceil(totalPageDb);

            QueryBuilder qbPage = dao.queryBuilder().offset(pageSize*currentPage).limit(pageSize);
            PreparedQuery<ChangzhouDataHistoryBean> pqPage = qbPage.where().between("date", start, end).prepare();
            final List<ChangzhouDataHistoryBean> itemsPage = dao.query(pqPage);
            if (mActivity!=null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvHistory.setData(itemsPage);
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
            RuntimeExceptionDao<ChangzhouDataHistoryBean, Long> dao = delegate.getDatabaseHelper().getChangzhouDataHistoryDao();
            QueryBuilder qbPage = dao.queryBuilder().offset(pageSize*currentPage).limit(pageSize);
            PreparedQuery<ChangzhouDataHistoryBean> pqPage = qbPage.where().between("date", start, end).prepare();
            List<ChangzhouDataHistoryBean> itemsPage = dao.query(pqPage);
            rvHistory.setData(itemsPage);
            currentText.setText(currentPage+1+"");
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
