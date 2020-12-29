/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.huawei.hms.ads.sdk

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.reward.Reward
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener
import com.huawei.hms.ads.sdk.RewardActivity
import java.util.*

/**
 * Activity for displaying a rewarded ad.
 */
class RewardActivity : BaseActivity() {
    private var rewardedTitle: TextView? = null
    private var scoreView: TextView? = null
    private var reStartButton: Button? = null
    private var watchAdButton: Button? = null
    private var rewardedAd: RewardAd? = null
    private var score = 1
    private val defaultScore = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.reward_ad)
        setContentView(R.layout.activity_reward)
        rewardedTitle = findViewById(R.id.text_reward)
        rewardedTitle!!.setText(R.string.reward_ad_title)

        // Load a rewarded ad.
        loadRewardAd()

        // Load a score view.
        loadScoreView()

        // Load the button for watching a rewarded ad.
        loadWatchButton()

        // Load the button for starting a game.
        loadPlayButton()
    }

    /**
     * Load a rewarded ad.
     */
    private fun loadRewardAd() {
        if (rewardedAd == null) {
            rewardedAd = RewardAd(this@RewardActivity, getString(R.string.ad_id_reward))
        }
        val rewardAdLoadListener: RewardAdLoadListener = object : RewardAdLoadListener() {
            override fun onRewardAdFailedToLoad(errorCode: Int) {
                Toast
                        .makeText(this@RewardActivity, "onRewardAdFailedToLoad errorCode is :$errorCode",
                                Toast.LENGTH_SHORT)
                        .show()
            }

            override fun onRewardedLoaded() {
                Toast.makeText(this@RewardActivity, "onRewardedLoaded", Toast.LENGTH_SHORT).show()
            }
        }
        rewardedAd!!.loadAd(AdParam.Builder().build(), rewardAdLoadListener)
    }

    /**
     * Display a rewarded ad.
     */
    private fun rewardAdShow() {
        if (rewardedAd!!.isLoaded) {
            rewardedAd!!.show(this@RewardActivity, object : RewardAdStatusListener() {
                override fun onRewardAdClosed() {
                    loadRewardAd()
                }

                override fun onRewardAdFailedToShow(errorCode: Int) {
                    Toast
                            .makeText(this@RewardActivity, "onRewardAdFailedToShow errorCode is :$errorCode",
                                    Toast.LENGTH_SHORT)
                            .show()
                }

                override fun onRewardAdOpened() {
                    Toast.makeText(this@RewardActivity, "onRewardAdOpened", Toast.LENGTH_SHORT).show()
                }

                override fun onRewarded(reward: Reward) {
                    // You are advised to grant a reward immediately and at the same time, check whether the reward
                    // takes effect on the server. If no reward information is configured, grant a reward based on the
                    // actual scenario.
                    val addScore = if (reward.amount == 0) defaultScore else reward.amount
                    Toast
                            .makeText(this@RewardActivity, "Watch video show finished , add $addScore scores",
                                    Toast.LENGTH_SHORT)
                            .show()
                    score += addScore
                    setScore(score)
                    loadRewardAd()
                }
            })
        }
    }

    /**
     * Set a score.
     *
     * @param score
     */
    private fun setScore(score: Int) {
        scoreView!!.text = "Score:$score"
    }

    /**
     * Load the button for watching a rewarded ad.
     */
    private fun loadWatchButton() {
        watchAdButton = findViewById(R.id.show_video_button)
        watchAdButton!!.setOnClickListener(View.OnClickListener { rewardAdShow() })
    }

    /**
     * Load the button for starting a game.
     */
    private fun loadPlayButton() {
        reStartButton = findViewById(R.id.play_button)
        reStartButton!!.setOnClickListener(View.OnClickListener { play() })
    }

    private fun loadScoreView() {
        scoreView = findViewById(R.id.score_count_text)
        scoreView!!.text = "Score:$score"
    }

    /**
     * Used to play a game.
     */
    private fun play() {
        // If the score is 0, a message is displayed, asking users to watch the ad in exchange for scores.
        if (score == 0) {
            Toast.makeText(this@RewardActivity, "Watch video ad to add score", Toast.LENGTH_SHORT).show()
            return
        }

        // The value 0 or 1 is returned randomly. If the value is 1, the score increases by 1. If the value is 0, the
        // score decreases by 5. If the score is a negative number, the score is set to 0.
        val random = Random().nextInt(RANGE)
        if (random == 1) {
            score += PLUS_SCORE
            Toast.makeText(this@RewardActivity, "You win！", Toast.LENGTH_SHORT).show()
        } else {
            score -= MINUS_SCORE
            score = if (score < 0) 0 else score
            Toast.makeText(this@RewardActivity, "You lose！", Toast.LENGTH_SHORT).show()
        }
        setScore(score)
    }

    companion object {
        private const val PLUS_SCORE = 1
        private const val MINUS_SCORE = 5
        private const val RANGE = 2
    }
}