package com.earth.angel.chat;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.earth.angel.gift.ui.UserMainActivity.USER_SHARE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.earth.angel.R;
import com.earth.angel.base.EarthAngelApp;
import com.earth.angel.util.Constants;
import com.earth.libbase.base.BaseApplication;
import com.earth.libbase.base.Constance;
import com.earth.libbase.base.PremissionObservable;
import com.earth.libbase.baseentity.BaseGiftEntity;
import com.earth.libbase.baseentity.ShipAddressEntity;
import com.earth.libbase.entity.GiftEntity;
import com.earth.libbase.util.BaseDateUtils;
import com.earth.libbase.util.PreferencesHelper;
import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMGroupAtInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tim.uikit.base.BaseFragment;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.component.AudioPlayer;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.AbsChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout;
import com.tencent.qcloud.tim.uikit.modules.forward.ForwardSelectActivity;
import com.tencent.qcloud.tim.uikit.modules.forward.base.ConversationBean;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.tencent.qcloud.tim.uikit.modules.group.info.StartGroupMemberSelectActivity;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class ChatFragment extends BaseFragment implements Observer {

    private View mBaseView;
    private ChatLayout mChatLayout;
    private TitleBarLayout mTitleBar;
    private ChatInfo mChatInfo;
    private GiftEntity mGiftEntity;

    private List<MessageInfo> mForwardSelectMsgInfos = null;
    private int mForwardMode;
    private String type = null;
    private static final String TAG = ChatFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        PremissionObservable.Companion.getMessageObservable().addObserver(this);
        mBaseView = inflater.inflate(R.layout.chat_fragment, container, false);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return mBaseView;
        }
        mChatInfo = (ChatInfo) bundle.getSerializable(Constants.CHAT_INFO);
        mGiftEntity = (GiftEntity) bundle.getSerializable(Constants.CHAT_CUSTOM);
        type = bundle.getString(USER_SHARE, "");
        if (mChatInfo == null) {
            return mBaseView;
        }
        initView();

        // TODO 通过api设置ChatLayout各种属性的样例
        ChatLayoutHelper helper = new ChatLayoutHelper(getActivity());
        helper.setGroupId(mChatInfo.getId());
        helper.customizeChatLayout(mChatLayout);
        if (mGiftEntity != null) {
            // I want this
            //  send(mGiftEntity);
        }
        if (!type.isEmpty()) {
            // forward my gift
            Gson gson = new Gson();
            CustomHelloMessage customHelloMessage = new CustomHelloMessage();
            customHelloMessage.version = TUIKitConstants.version;
            customHelloMessage.text = getString(R.string.text_Check_my);
            //   customHelloMessage.customCellType = "1";
            customHelloMessage.userName = PreferencesHelper.INSTANCE.getName(requireActivity());
            //   customHelloMessage.identificationID = PreferencesHelper.INSTANCE.getUserId(requireActivity());
            //  customHelloMessage.link = PreferencesHelper.INSTANCE.getHead(requireActivity());
            String data = gson.toJson(customHelloMessage); // data = {"businessID":"text_link","link":"https://cloud.tencent.com/document/product/269/3794","text":"欢迎加入云通信IM大家庭！","version":4}
            // 根据 JSON 创建自定义消息，会调用上面重写的 createCommonInfoFromTimMessage 方法解析出 MessageInfo
            MessageInfo info = MessageInfoUtil.buildCustomMessage(data);
            mChatLayout.sendMessage(info, false);
        }
        return mBaseView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        PremissionObservable.Companion.getMessageObservable().deleteObserver(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CustomHelloMessage event) {
        //处理逻辑
        Gson gson = new Gson();
      /*  CustomHelloMessage customHelloMessage = new CustomHelloMessage();
        customHelloMessage.version = TUIKitConstants.version;
        customHelloMessage.text =mGiftEntity.getDescription();
        if(mGiftEntity.getPictureResources().size()>0){
            customHelloMessage.link = mGiftEntity.getPictureResources().get(0).getPictureUrl();
        }
        customHelloMessage.vue=mGiftEntity.getValuation()+"";*/
        String data = gson.toJson(event); // data = {"businessID":"text_link","link":"https://cloud.tencent.com/document/product/269/3794","text":"欢迎加入云通信IM大家庭！","version":4}
// 根据 JSON 创建自定义消息，会调用上面重写的 createCommonInfoFromTimMessage 方法解析出 MessageInfo
        MessageInfo info = MessageInfoUtil.buildCustomMessage(data);
        mChatLayout.sendMessage(info, false);
       /* if(!event.getIsSend().equals("")){
            MessageInfo textinfo;
            if(event.getIsSend().equals("0")){
                textinfo = MessageInfoUtil.buildTextMessage("No, give me some time to think about it.");
            }else {
                textinfo = MessageInfoUtil.buildTextMessage("Yes, we can agree on a place and time to meet.");
            }
            mChatLayout.sendMessage(textinfo, false);
            mChatLayout.getMessageLayout().scrollToEnd();
        }*/


    }

    private void initView() {
        //从布局文件中获取聊天面板组件
        mChatLayout = mBaseView.findViewById(R.id.chat_layout);
        //单聊组件的默认UI和交互初始化
        mChatLayout.initDefault();
        /*
         * 需要聊天的基本信息
         */
        mChatLayout.setChatInfo(mChatInfo);

        //获取单聊面板的标题栏
        mTitleBar = mChatLayout.getTitleBar();
        mTitleBar.setVisibility(GONE);
        mTitleBar.setLeftIcon(R.mipmap.bank);
        mTitleBar.setRightIcon(0);
        //单聊面板标记栏返回按钮点击事件，这里需要开发者自行控制
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        if (mChatInfo.getType() == V2TIMConversation.V2TIM_C2C) {
            mTitleBar.setOnRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
          /*        Intent intent = new Intent( EarthAngelApp.instance, FriendProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(TUIKitConstants.ProfileType.CONTENT, mChatInfo);
                    EarthAngelApp.instance.startActivity(intent);*/
                }
            });
        }
        mChatLayout.setForwardSelectActivityListener(new AbsChatLayout.onForwardSelectActivityListener() {
            @Override
            public void onStartForwardSelectActivity(int mode, List<MessageInfo> msgIds) {
                mForwardMode = mode;
                mForwardSelectMsgInfos = msgIds;

                Intent intent = new Intent(EarthAngelApp.instance, ForwardSelectActivity.class);
                intent.putExtra(ForwardSelectActivity.FORWARD_MODE, mode);
                startActivityForResult(intent, TUIKitConstants.FORWARD_SELECT_ACTIVTY_CODE);
            }
        });

        mChatLayout.getMessageLayout().setOnItemClickListener(new MessageLayout.OnItemLongClickListener() {
            @Override
            public void onMessageLongClick(View view, int position, MessageInfo messageInfo) {
                //因为adapter中第一条为加载条目，位置需减1
                mChatLayout.getMessageLayout().showItemPopMenu(position - 1, messageInfo, view);
            }

            @Override
            public void onUserIconClick(View view, int position, MessageInfo messageInfo) {
                if (null == messageInfo) {
                    return;
                }

                if (BaseDateUtils.INSTANCE.isFastClick()) {
                    ARouter.getInstance().build(Constance.LibMineUserActivityURL)
                            .withString("userId", messageInfo.getFromUser())
                            .navigation();
                }
          /*      V2TIMMergerElem mergerElem = messageInfo.getTimMessage().getMergerElem();
                if (mergerElem != null){
                    Intent intent = new Intent( EarthAngelApp.instance, ForwardChatActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable(TUIKitConstants.FORWARD_MERGE_MESSAGE_KEY, messageInfo);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    EarthAngelApp.instance.startActivity(intent);
                }else {
                    ChatInfo info = new ChatInfo();
                    info.setId(messageInfo.getFromUser());
                    Intent intent = new Intent(EarthAngelApp.instance, FriendProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(TUIKitConstants.ProfileType.CONTENT, info);
                    EarthAngelApp.instance.startActivity(intent);
                }*/
            }
        });
        mChatLayout.getInputLayout().setStartActivityListener(new InputLayout.OnStartActivityListener() {
            @Override
            public void onStartGroupMemberSelectActivity() {
                Intent intent = new Intent(EarthAngelApp.instance, StartGroupMemberSelectActivity.class);
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setId(mChatInfo.getId());
                groupInfo.setChatName(mChatInfo.getChatName());
                intent.putExtra(TUIKitConstants.Group.GROUP_INFO, groupInfo);
                startActivityForResult(intent, 1);
            }
        });

        if (false/*mChatInfo.getType() == V2TIMConversation.V2TIM_GROUP*/) {
            V2TIMManager.getConversationManager().getConversation(mChatInfo.getId(), new V2TIMValueCallback<V2TIMConversation>() {
                @Override
                public void onError(int code, String desc) {
                    Log.e(TAG, "getConversation error:" + code + ", desc:" + desc);
                }

                @Override
                public void onSuccess(V2TIMConversation v2TIMConversation) {
                    if (v2TIMConversation == null) {
                        // DemoLog.d(TAG,"getConversation failed");
                        return;
                    }
                    mChatInfo.setAtInfoList(v2TIMConversation.getGroupAtInfoList());
                    final V2TIMMessage lastMessage = v2TIMConversation.getLastMessage();
                    updateAtInfoLayout();
                    mChatLayout.getAtInfoLayout().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final List<V2TIMGroupAtInfo> atInfoList = mChatInfo.getAtInfoList();
                            if (atInfoList == null || atInfoList.isEmpty()) {
                                mChatLayout.getAtInfoLayout().setVisibility(GONE);
                                return;
                            } else {
                                mChatLayout.getChatManager().getAtInfoChatMessages(atInfoList.get(atInfoList.size() - 1).getSeq(), lastMessage, new IUIKitCallBack() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        mChatLayout.getMessageLayout().scrollToPosition((int) atInfoList.get(atInfoList.size() - 1).getSeq());
                                        LinearLayoutManager mLayoutManager = (LinearLayoutManager) mChatLayout.getMessageLayout().getLayoutManager();
                                        mLayoutManager.scrollToPositionWithOffset((int) atInfoList.get(atInfoList.size() - 1).getSeq(), 0);

                                        atInfoList.remove(atInfoList.size() - 1);
                                        mChatInfo.setAtInfoList(atInfoList);

                                        updateAtInfoLayout();
                                    }

                                    @Override
                                    public void onError(String module, int errCode, String errMsg) {
                                        //DemoLog.d(TAG,"getAtInfoChatMessages failed");
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    private void updateAtInfoLayout() {
        int atInfoType = getAtInfoType(mChatInfo.getAtInfoList());
        switch (atInfoType) {
            case V2TIMGroupAtInfo.TIM_AT_ME:
                mChatLayout.getAtInfoLayout().setVisibility(VISIBLE);
                mChatLayout.getAtInfoLayout().setText(EarthAngelApp.instance.getString(R.string.ui_at_me));
                break;
            case V2TIMGroupAtInfo.TIM_AT_ALL:
                mChatLayout.getAtInfoLayout().setVisibility(VISIBLE);
                mChatLayout.getAtInfoLayout().setText(EarthAngelApp.instance.getString(R.string.ui_at_all));
                break;
            case V2TIMGroupAtInfo.TIM_AT_ALL_AT_ME:
                mChatLayout.getAtInfoLayout().setVisibility(VISIBLE);
                mChatLayout.getAtInfoLayout().setText(EarthAngelApp.instance.getString(R.string.ui_at_all_me));
                break;
            default:
                mChatLayout.getAtInfoLayout().setVisibility(GONE);
                break;

        }
    }

    private int getAtInfoType(List<V2TIMGroupAtInfo> atInfoList) {
        int atInfoType = 0;
        boolean atMe = false;
        boolean atAll = false;

        if (atInfoList == null || atInfoList.isEmpty()) {
            return V2TIMGroupAtInfo.TIM_AT_UNKNOWN;
        }

        for (V2TIMGroupAtInfo atInfo : atInfoList) {
            if (atInfo.getAtType() == V2TIMGroupAtInfo.TIM_AT_ME) {
                atMe = true;
                continue;
            }
            if (atInfo.getAtType() == V2TIMGroupAtInfo.TIM_AT_ALL) {
                atAll = true;
                continue;
            }
            if (atInfo.getAtType() == V2TIMGroupAtInfo.TIM_AT_ALL_AT_ME) {
                atMe = true;
                atAll = true;
                continue;
            }
        }

        if (atAll && atMe) {
            atInfoType = V2TIMGroupAtInfo.TIM_AT_ALL_AT_ME;
        } else if (atAll) {
            atInfoType = V2TIMGroupAtInfo.TIM_AT_ALL;
        } else if (atMe) {
            atInfoType = V2TIMGroupAtInfo.TIM_AT_ME;
        } else {
            atInfoType = V2TIMGroupAtInfo.TIM_AT_UNKNOWN;
        }

        return atInfoType;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 3) {
            String result_id = data.getStringExtra(TUIKitConstants.Selection.USER_ID_SELECT);
            String result_name = data.getStringExtra(TUIKitConstants.Selection.USER_NAMECARD_SELECT);
            mChatLayout.getInputLayout().updateInputText(result_name, result_id);
        } else if (requestCode == TUIKitConstants.FORWARD_SELECT_ACTIVTY_CODE && resultCode == TUIKitConstants.FORWARD_SELECT_ACTIVTY_CODE) {
            if (data != null) {
                if (mForwardSelectMsgInfos == null || mForwardSelectMsgInfos.isEmpty()) {
                    return;
                }

                ArrayList<ConversationBean> conversationBeans = data.getParcelableArrayListExtra(TUIKitConstants.FORWARD_SELECT_CONVERSATION_KEY);
                if (conversationBeans == null || conversationBeans.isEmpty()) {
                    return;
                }

                for (int i = 0; i < conversationBeans.size(); i++) {//遍历发送对象会话
                    boolean isGroup = conversationBeans.get(i).getIsGroup() == 1;
                    String id = conversationBeans.get(i).getConversationID();
                    String title = "";
                    if (mChatInfo.getType() == V2TIMConversation.V2TIM_GROUP) {
                        title = mChatInfo.getId() + getString(R.string.forward_chats);
                    } else {
                        title = V2TIMManager.getInstance().getLoginUser() + getString(R.string.and_text) + mChatInfo.getId() + getString(R.string.forward_chats_c2c);
                    }

                    boolean selfConversation = false;
                    if (id != null && id.equals(mChatInfo.getId())) {
                        selfConversation = true;
                    }

                    ChatManagerKit chatManagerKit = mChatLayout.getChatManager();
                    chatManagerKit.forwardMessage(mForwardSelectMsgInfos, isGroup, id, title, mForwardMode, selfConversation, false, new IUIKitCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            // DemoLog.v(TAG, "sendMessage onSuccess:");
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {
                            //   DemoLog.v(TAG, "sendMessage fail:" + errCode + "=" + errMsg);
                        }
                    });
                }
            }
        }
    }

    private List<V2TIMMessage> MessgeInfo2TIMMessage(List<MessageInfo> msgInfos) {
        if (msgInfos == null || msgInfos.isEmpty()) {
            return null;
        }
        List<V2TIMMessage> msgList = new ArrayList<>();
        for (int i = 0; i < msgInfos.size(); i++) {
            msgList.add(msgInfos.get(i).getTimMessage());
        }
        return msgList;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChatLayout != null && mChatLayout.getChatManager() != null) {
            mChatLayout.getChatManager().setChatFragmentShow(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mChatLayout != null) {
            if (mChatLayout.getInputLayout() != null) {
                mChatLayout.getInputLayout().setDraft();
            }
            if (mChatLayout.getChatManager() != null) {
                mChatLayout.getChatManager().setChatFragmentShow(false);
            }
        }
        AudioPlayer.getInstance().stopPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatLayout != null) {
            mChatLayout.exitChat();
        }
    }

    public void sendCustom(ArrayList<BaseGiftEntity> mList, String str) {
        Gson gson = new Gson();
        CustomHelloMessage customHelloMessage = new CustomHelloMessage();
        customHelloMessage.version = TUIKitConstants.version;
        customHelloMessage.text = str;
        customHelloMessage.type = CustomHelloMessage.WANT;
        //    customHelloMessage.customCellType = "0";
        customHelloMessage.formUser = BaseApplication.Companion.getMyBaseUser().getUserId();
        //   customHelloMessage.identificationID ="33";
        customHelloMessage.list.addAll(mList);
        /*if (mGiftEntity.getPictureResources().size() > 0) {
            customHelloMessage.link = mGiftEntity.getPictureResources().get(0).getPictureUrl();
        }*/
        String data = gson.toJson(customHelloMessage); // data = {"businessID":"text_link","link":"https://cloud.tencent.com/document/product/269/3794","text":"欢迎加入云通信IM大家庭！","version":4}
// 根据 JSON 创建自定义消息，会调用上面重写的 createCommonInfoFromTimMessage 方法解析出 MessageInfo
        MessageInfo info = MessageInfoUtil.buildCustomMessage(data);
        mChatLayout.sendMessage(info, false);
    }

    public void sendAddressCustom(ShipAddressEntity shipAddressEntity, String str) {
        Gson gson = new Gson();
        CustomHelloMessage customHelloMessage = new CustomHelloMessage();
        customHelloMessage.version = TUIKitConstants.version;
        customHelloMessage.text = str;
        customHelloMessage.type = CustomHelloMessage.ADDRESS;
        //    customHelloMessage.customCellType = "0";
        customHelloMessage.formUser = BaseApplication.Companion.getMyBaseUser().getUserId();
        //   customHelloMessage.identificationID ="33";
        customHelloMessage.shipAddress = shipAddressEntity;
        /*if (mGiftEntity.getPictureResources().size() > 0) {
            customHelloMessage.link = mGiftEntity.getPictureResources().get(0).getPictureUrl();
        }*/
        String data = gson.toJson(customHelloMessage); // data = {"businessID":"text_link","link":"https://cloud.tencent.com/document/product/269/3794","text":"欢迎加入云通信IM大家庭！","version":4}
// 根据 JSON 创建自定义消息，会调用上面重写的 createCommonInfoFromTimMessage 方法解析出 MessageInfo
        MessageInfo info = MessageInfoUtil.buildCustomMessage(data);
        mChatLayout.sendMessage(info, false);
    }


    public void scrollToEnd() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                mChatLayout.getMessageLayout().scrollToEnd();
            }
        }, 1000);


    }

    @Override
    public void update(Observable o, Object arg) {
        if (o != null) {
            if (o instanceof PremissionObservable) {
                if (arg != null) {
                    String mArg=(String)arg;
                    if(mArg!=null){
                        requestPermissionLauncher.launch(
                                mArg);
                    }
                }
            }
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

}
