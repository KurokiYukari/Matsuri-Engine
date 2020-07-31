package cn.edu.cuc.kuroki.matsuri.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.cuc.kuroki.matsuri.R;
import cn.edu.cuc.kuroki.matsuri.utils.MainUtils;

/**
 * Gal Fragment
 * 游戏的进行界面，交互方式同传统的 GalGame 一样，提供 Q.S Q.L 进行 S/L
 * 有内部类 ScriptController，负责解释脚本并且更新 GalFragment
 *      ps: 计划在 cg 变换和立绘变换时加入补间动画，让画面进行更圆滑一点qwq
 */
public class GalFragment extends Fragment {
    private ScriptController scriptController;
    private boolean isLoad;

    // views
    private ConstraintLayout container_constraintLayout;
    private ConstraintLayout temporary_constraintLayout;
    private ConstraintLayout main_constraintLayout;
    private ImageView main_imageView;
    private TextView name_textView;
    private TextView main_textView;
    private ImageView spL_imageView;
    private ImageView spC_imageView;
    private ImageView spR_imageView;
    private TextView cfg_button;
    private TextView save_button;
    private TextView load_button;

    /**
     * 默认构造函数，从头开始游戏
     */
    public GalFragment() {
        scriptController = new ScriptController("mainScript.txt");
        isLoad = false;
    }

    /**
     * 继续游戏的构造函数，初始化时将脚本定位到存档所指向的位置上
     * @param save pointer
     * @param savedBGM BGM
     * @param savedCG CG
     */
    public GalFragment(int save, String savedBGM, String savedCG) {
        this();
        scriptController.loadData(save, savedBGM, savedCG);
        isLoad = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gal, container, false);
        bindViews(view);

        main_constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 每次 OnClick，调用 脚本解释器 的 update() 进行更新
                scriptController.update();
            }
        });

        cfg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FadeIn and show temporary_constraint
                FragmentManager fragmentManager = MainUtils.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.temporary_constraintLayout, new ConfigFragment());
                fragmentTransaction.commit();

                temporary_constraintLayout.startAnimation(MainUtils.getFadeInAnimation(1600));
                temporary_constraintLayout.setVisibility(View.VISIBLE);
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save pointer, BGM & CG info to SharedPreferences "save"
                SharedPreferences srpf = MainUtils.getContext().getSharedPreferences("save", Context.MODE_PRIVATE);
                srpf.edit().putInt("qs", scriptController.getIndex()).putString("qsBGM", scriptController.getCurrentBGM()).putString("qsCG", scriptController.getCurrentCG()).apply();
            }
        });

        load_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load
                // see TitleFragment continue_button OnClick

                AlphaAnimation alphaAnimation = MainUtils.getFadeOutAnimation(1500);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        SharedPreferences srpf = MainUtils.getContext().getSharedPreferences("save", Context.MODE_PRIVATE);
                        int save = srpf.getInt("qs", 0);
                        save--;
                        String savedBGM = srpf.getString("qsBGM", "Destiny");
                        String savedCG = srpf.getString("qsCG", "その他_血の匂いを覚えている_01");

                        FragmentManager fragmentManager = MainUtils.getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainFragmentContainer_constraintLayout, new GalFragment(save, savedBGM, savedCG));
                        fragmentTransaction.commit();

                        container_constraintLayout.startAnimation(MainUtils.getFadeInAnimation(1500));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                container_constraintLayout.startAnimation(alphaAnimation);
            }
        });

        // 每次 update 会使得 BGM CG 信息被覆盖，在初始化时如果是通过 Load 进入的 GalFragment，则还要调用 reLoadBGMnCG 将被覆盖的信息重新加载
        scriptController.update();
        if (isLoad) {
            scriptController.reLoadBGMnCG();
        }

        return view;
    }

    private void bindViews(View view) {
        container_constraintLayout = getActivity().findViewById(R.id.mainFragmentContainer_constraintLayout);
        temporary_constraintLayout = getActivity().findViewById(R.id.temporary_constraintLayout);
        main_constraintLayout = view.findViewById(R.id.galMain_constraintLayout);
        main_imageView = view.findViewById(R.id.galMain_imageView);
        name_textView = view.findViewById(R.id.galName_textView);
        main_textView = view.findViewById(R.id.galMain_textView);
        spL_imageView = view.findViewById(R.id.spLeft_imageView);
        spC_imageView = view.findViewById(R.id.spCenter_imageView);
        spR_imageView = view.findViewById(R.id.spRight_imageView);
        cfg_button = view.findViewById(R.id.galCfg_button);
        save_button = view.findViewById(R.id.galSave_button);
        load_button = view.findViewById(R.id.galLoad_button);
    }

    /**
     * 脚本控制器类，默认读取 assets 文件夹下的 mainScript.txt 作为脚本
     * 脚本支持四种指令 ps: ( ) 中的代表具体的内容：
     *      [MSG:(null | msgName)_(msgContent)]     在 textBox 上输出一条剧本消息 (null | msgName) 代表剧本的叙述者（null 为无，一般代表旁白之类的），(msgContent) 为具体的剧本内容
     *      [CG:(cgName)]   显示 cg (cgName) 为 cg 在 assets 中的文件名（不包括后缀），格式要求为 jpg
     *      [BGM:(bgmName)] 播放 bgm (bgmName) 为 音频文件 在 assets 中的文件名（不包括后缀），格式要求为 mp3
     *      [+(spName)_(spPos)] 显示立绘 (spName) 为 立绘 在 assets 中的文件名（不包括后缀），格式要求为 png，(spPos) 为立绘位置，可以是 0 1 2 （代表 左 中 右）
     * 其中 MSG 和 + 指令为瞬时指令（只在这次 OnClick 中有效，下一次 OnClick 时会将之前的信息全清空）；CG 和 BGM 指令为持续指令（直到下一次使用同类指令时上一个指令才失效）
     * 每一行代表一次 OnClick 要执行的所有指令
     */
    class ScriptController {
        private AssetManager assetManager = MainUtils.getContext().getAssets();

        // script & pointer
        private ArrayList<String> script;
        private int index = -1;

        // cmd
        private String commandBGM = null;
        private String commandCG = null;
        private String commandMSG = null;
        private Vector<String> commadnSPs = new Vector<>();

        // saved cmd
        private String currentBGM = null;
        private String currentCG = null;

        /**
         * 通过 scriptName 构造一个 ScriptController，将其按行拆分
         * @param scriptName scriptName
         */
        ScriptController(String scriptName) {
            AssetManager assetManager = MainUtils.getContext().getAssets();

            // 使用ArrayList来存储每行读取到的字符串
            this.script = new ArrayList<>();
            try {
                InputStream is = assetManager.open(scriptName);
                InputStreamReader isr = new InputStreamReader(is, "GBK");
                BufferedReader bf = new BufferedReader(isr);
                String str;
                // 按行读取字符串
                while ((str = bf.readLine()) != null) {
                    this.script.add(str);
                }
                bf.close();
                isr.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 一次 OnClick 执行的操作
         */
        void update() {
            index++;
            analyseRow();

            // BGM control
            if (this.commandBGM != null) {
                MainUtils.getBGMBinder().prepareAndStart(commandBGM + ".mp3");
                this.commandBGM = null;
            }

            // CG control
            if (this.commandCG != null) {
                main_imageView.setImageDrawable(loadDrawable(commandCG + ".jpg"));
                this.commandCG = null;
            }

            // SP control
            spL_imageView.setImageDrawable(MainUtils.getContext().getResources().getDrawable(R.color.transparent));
            spC_imageView.setImageDrawable(MainUtils.getContext().getResources().getDrawable(R.color.transparent));
            spR_imageView.setImageDrawable(MainUtils.getContext().getResources().getDrawable(R.color.transparent));
            if (!this.commadnSPs.isEmpty()) {
                for (String sp : commadnSPs) {
                    String spName = sp.substring(0, sp.length() - 2) + ".png";
                    int pos = Integer.valueOf(sp.substring(sp.length() - 1, sp.length()));

                    switch (pos) {
                        case 0:
                            spL_imageView.setImageDrawable(loadDrawable(spName));
                            break;
                        case 1:
                            spC_imageView.setImageDrawable(loadDrawable(spName));
                            break;
                        case 2:
                            spR_imageView.setImageDrawable(loadDrawable(spName));
                            break;
                    }
                }
            }

            // MSG control
            if (this.commandMSG != null) {
                String[] split = this.commandMSG.split("_");
                if (!split[0].equals("null")) {
                    name_textView.setText(split[0]);
                } else {
                    name_textView.setText("");
                }

                main_textView.setText(split[1]);
            }

            if (this.index == this.script.size()) {
                AlphaAnimation alphaAnimation = MainUtils.getFadeOutAnimation(1500);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        FragmentManager fragmentManager = MainUtils.getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainFragmentContainer_constraintLayout, new TitleFragment());
                        fragmentTransaction.commit();

                        container_constraintLayout.startAnimation(MainUtils.getFadeInAnimation(1500));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                container_constraintLayout.startAnimation(alphaAnimation);
            }
        }

        private void analyseRow() {
            if (this.index < this.script.size()) {
                // get command
                String line = this.script.get(index);

                Pattern bgmPattern = Pattern.compile("\\[BGM:(.*?)\\]");
                Pattern cgPattern = Pattern.compile("\\[CG:(.*?)\\]");
                Pattern msgPattern = Pattern.compile("\\[MSG:(.*?)\\]");
                Pattern spgPattern = Pattern.compile("(\\[\\+.*\\])");

                Matcher m = bgmPattern.matcher(line);
                if (m.find()) {
                    this.commandBGM = m.group(1);
                    this.currentBGM = this.commandBGM;
                } else {
                    this.commandBGM = null;
                }

                m = cgPattern.matcher(line);
                if (m.find()) {
                    this.commandCG = m.group(1);
                    this.currentCG = this.commandCG;
                } else {
                    this.commandCG = null;
                }

                m = msgPattern.matcher(line);
                if (m.find()) {
                    this.commandMSG = m.group(1);
                }

                m = spgPattern.matcher(line);
                if (m.find()) {
                    this.commadnSPs.clear();
                    String[] SPs = m.group(1).split("\t");
                    for (String string : SPs) {
                        this.commadnSPs.add(string.substring(2, string.length() - 1));
                    }
                } else {
                    this.commadnSPs.clear();
                }
                // get command END
            }
        }

        private Drawable loadDrawable(String imgName) {
            InputStream is = null;
            Drawable cg_drawable = null;
            try {
                is = assetManager.open(imgName);
                cg_drawable = Drawable.createFromStream(is, null);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return cg_drawable;
        }

        void loadData(int index, String currentBGM, String currentCG) {
            this.index = index;
            this.currentBGM = currentBGM;
            this.currentCG = currentCG;
        }

        void reLoadBGMnCG() {
            // BGM control
            if (this.currentBGM != null) {
                MainUtils.getBGMBinder().prepareAndStart(currentBGM + ".mp3");
            }

            // CG control
            if (this.currentCG != null) {
                main_imageView.setImageDrawable(loadDrawable(currentCG + ".jpg"));
            }
        }

        int getIndex() {
            return index;
        }

        String getCurrentBGM() {
            return currentBGM;
        }

        String getCurrentCG() {
            return currentCG;
        }
    }
}
