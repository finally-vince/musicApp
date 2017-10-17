package com.moliying.mlymusicapp.fragment;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.moliying.mlymusicapp.R;
import com.moliying.mlymusicapp.adapter.CommonFragmentAdapter;
import com.moliying.mlymusicapp.utils.MessageEventType;
import com.moliying.mlymusicapp.vo.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndexFragment extends Fragment {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TabLayout tab;
    private ViewPager viewPager;
    private String[] titles = null;
    private NavigationView navigationView;
    private FragmentManager fm;
    public IndexFragment() {
        // Required empty public constructor
    }

    public static IndexFragment newInstance() {
        Bundle args = new Bundle();

        IndexFragment fragment = new IndexFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_index, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        drawer = (DrawerLayout) view.findViewById(R.id.drawer);
        tab = (TabLayout) view.findViewById(R.id.tab);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        navigationView = (NavigationView) view.findViewById(R.id.navigationView);
        View v = navigationView.getHeaderView(0);
        v.findViewById(R.id.user_face).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "icon", Toast.LENGTH_SHORT).show();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_item_fav:
                        EventBus.getDefault().post(
                                new MessageEvent(
                                        MessageEventType.SHOW_MY_LIKE_MUSIC_LIST_FRAGMENT));
                        drawer.closeDrawers();
                        break;
                    case R.id.menu_item_playinglist:
                        Toast.makeText(getActivity(), "播放列表正在开发中", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_item_listmore:
                        EventBus.getDefault().post(
                                new MessageEvent(
                                        MessageEventType.SHOW_LATELY_PLAY_LIST_FRAGMENT));
                        drawer.closeDrawers();
                        break;
                    case R.id.exit:
                        EventBus.getDefault().post(new MessageEvent(MessageEventType.EXIT_APP));
                        break;
                }
                return false;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer,toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        titles = getResources().getStringArray(R.array.tabs);
        fm = getActivity().getSupportFragmentManager();
        CommonFragmentAdapter adapter = new CommonFragmentAdapter(
                getActivity().getSupportFragmentManager(),titles,initFragment());
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);
        return view;
    }

    private Fragment[] initFragment() {
        return new Fragment[]{
                LocalMusicFragment.newInstance(),
                NetMusicLibsFragment.newInstance()};
    }
}
