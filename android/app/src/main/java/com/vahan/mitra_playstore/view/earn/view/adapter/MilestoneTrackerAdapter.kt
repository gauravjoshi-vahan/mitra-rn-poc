package com.vahan.mitra_playstore.view.earn.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.VerticalMilestoneStatusBinding
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter
import com.vahan.mitra_playstore.view.ratecard.ui.RateCardDetailViewOnClick


class MilestoneTrackerAdapter(
    private val context: Context,
    private val listener: RateCardDetailViewOnClick,
//    private val milestoneModel: MilestoneResponseModel
    private val milestoneData: EarnDataModel.IncentiveStructures,
) : RecyclerView.Adapter<MilestoneTrackerAdapter.MyViewHolder>() {
    class MyViewHolder(milestoneStatusBinding: VerticalMilestoneStatusBinding) :
        RecyclerView.ViewHolder(milestoneStatusBinding.root) {
        val binding: VerticalMilestoneStatusBinding = milestoneStatusBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val milestoneStatusBinding: VerticalMilestoneStatusBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.vertical_milestone_status, parent, false
        )
        return MyViewHolder(milestoneStatusBinding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        holder.binding.apply {
            val itemList = milestoneData.incentiveList
            val item = milestoneData.incentiveList[position]
            var currentTrips = arrayListOf<Int>()
            var milestoneTrips = arrayListOf<Int>()
            val allMilestones = item.milestones
            val len = item.milestones.size
            val isMitra = itemList[position].spinnerKey == "Mitra"

            tncTxt1.text = Html.fromHtml(milestoneData.incentiveList[position].milestoneFooter?.label?:"")
            cardHeaderBottomTxt.text = context.resources.getString(R.string.updated_till) + "${item.lastUpdatedTimeStamp}";
            cardHeaderTxt.text = item.companyTitle ?: "";
            tripsCompletedTxt.text = item.companies?.get(0)?.targetAchieved.toString() + context.resources.getString(R.string.trips_completed)
            item.companies?.get(0)?.companyIcon?.let { setImage(it, tripsCompletedImg) }

            tripsCompletedRightTxt.setOnClickListener {
                milestoneData.let { listener.onClick(position) }
            }
            tncContainer.setOnClickListener{ view ->
                val bundle = Bundle()
                bundle.putString("label", item.milestoneFooter?.label)
                bundle.putString("description", item.milestoneFooter?.description)
                Navigation.findNavController(view).navigate(R.id.terms_and_condition_fragment, bundle)
            }

            tncContainer.isEnabled = milestoneData.incentiveList[position].milestoneFooter?.description!=null
            if (isMitra) {
                tripsCompletedMitraContent.visibility = View.VISIBLE
                tripsCompletedMitraTxt.text =
                    item.companies?.get(1)?.targetAchieved.toString() + context.resources.getString(R.string.trips_completed)
                item.companies?.get(1)?.companyIcon?.let { setImage(it, tripsCompletedMitraImg) }
                item.companies?.get(0)?.targetAchieved?.let { currentTrips.add(it) }
                item.companies?.get(1)?.targetAchieved?.let { currentTrips.add(it) }

            } else {
                item.companies?.get(0)?.targetAchieved?.let { currentTrips.add(it) }
            }

            setCardData(holder, "third", "first", item, allMilestones[len - 1])
            for ((index, milestone) in allMilestones.withIndex()) {
                milestoneTrips.clear()
                if (isMitra) {
                    milestone.companyTargets?.get(0)?.target?.let {
                        milestoneTrips.add(it)
                    }
                    milestone.companyTargets?.get(1)?.target?.let {
                        milestoneTrips.add(it)
                    }

                } else {
                    milestone.companyTargets?.get(0)?.target?.let {
                        milestoneTrips.add(it)
                    }
                }
                if (milestone.achieved == "IN_PROGRESS") {
                    if (item.milestones.size <= 3) {
                        if (item.milestones.size == 3) {

                            displayCardsNoDots(index,
                                len,
                                holder,
                                item,
                                allMilestones,
                                currentTrips,
                                milestoneTrips)
                        }
                    }
                    if (index <= 1) {
                        if (index == 0) {
                            onFirst.visibility = View.VISIBLE
                            firstDotImg.visibility = View.GONE
                            setCardData(holder,
                                "first",
                                "first",
                                item,
                                allMilestones[0])
                            setCardData(holder,
                                "second",
                                "first",
                                item,
                                allMilestones[1])
                        } else if (index == 1) {
                            firstDotImg.setImageResource(R.drawable.ic_ellipse_124)
                            cardNotOnFirstMitraCompletedImg.visibility = View.VISIBLE
                            setCardData(holder, "first", "second", item, allMilestones[0])
                            setCardData(holder, "second", "second", item, allMilestones[1])
                            if (currentTrips === milestoneTrips) {
                                onSecond.visibility = View.VISIBLE
                                secondDotImg.visibility = View.GONE
                            } else {
                                betweenFirstAndSecond.visibility = View.VISIBLE
                            }
                        }
                    } else if (index > 1 && index < (len - 3)) {
                        topLineImg.visibility = View.VISIBLE

                        if (index % 2 == 0) {
                            onFirst.visibility = View.VISIBLE
                            firstDotImg.visibility = View.GONE
                            setCardData(holder,
                                "first",
                                "first",
                                item,
                                allMilestones[index])
                            setCardData(holder,
                                "second",
                                "first",
                                item,
                                allMilestones[index + 1])
                        } else {
                            firstDotImg.setImageResource(R.drawable.ic_ellipse_124)
                            cardNotOnFirstMitraCompletedImg.visibility = View.VISIBLE
                            setCardData(holder,
                                "first",
                                "second",
                                item,
                                allMilestones[index - 1])
                            setCardData(holder,
                                "second",
                                "second",
                                item,
                                allMilestones[index])
                            if (currentTrips === milestoneTrips) {
                                onSecond.visibility = View.VISIBLE
                            } else {
                                betweenFirstAndSecond.visibility = View.VISIBLE
                            }
                        }
                    } else if (index >= (len - 3)) {
                        topLineImg.visibility = View.VISIBLE
                        displayCardsNoDots(index,
                            len,
                            holder,
                            item,
                            allMilestones,
                            currentTrips,
                            milestoneTrips)
                    }
                    break;
                } else if (index == (len - 1) && milestone.achieved == "COMPLETED") {
                    if (item.milestones.size >= 3)
                        topLineImg.visibility = View.VISIBLE

                    cardNotOnFirstMitraCompletedImg.visibility = View.VISIBLE
                    cardNotOnSecondMitraCompletedImg.visibility = View.VISIBLE
                    cardOnThirdMitraCompletedImg.visibility = View.VISIBLE
                    displayCardsNoDots(index,
                        len,
                        holder,
                        item,
                        allMilestones,
                        currentTrips,
                        milestoneTrips,
                        isMitra
                    )
                }
            }
        }
    }

    private fun displayCardsNoDots(
        index: Int,
        len: Int,
        holder: MyViewHolder,
        item: EarnDataModel.IncentiveStructures.IncentiveList,
        allMilestones: List<EarnDataModel.IncentiveStructures.IncentiveList.Milestone>,
        currentTrips: ArrayList<Int>,
        milestoneTrips: ArrayList<Int>,
        isMitra: Boolean = false,
    ) {
        holder.binding.apply {
            if (allMilestones.size <= 3 || index >= (len - 3)) {
                threeDotsCard.visibility = View.GONE
                threeDotsLine.visibility = View.GONE
            }
            if (index !== (len - 1)) {
                secondDot.layoutParams = setWeights(0.4f)
                threeDotsParent.layoutParams = setWeights(0.15f)
                cardOnFirstParent.layoutParams = setWeights(0.48f, ViewGroup.LayoutParams.MATCH_PARENT)
                threeDotsCardParent.layoutParams = setWeights(0.35f)
            }
            else{
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 25, 0, 0)
                cardNotOnSecond.layoutParams = params
                secondDot.layoutParams = setWeights(0.25f)
//                threeDotsParent.layoutParams = setWeights(0.3f)
                threeDotsCardParent.layoutParams = setWeights(0.35f)
            }

            var pos1 = 0
            var pos2 = 1
            var pos3 = 2

            if (index == (len - 3)) {
                onFirst.visibility = View.VISIBLE
                firstDotImg.visibility = View.GONE
                if (len > 3) {
                    pos1 = index
                    pos2 = index + 1
                }
                setCardData(holder,
                    "first",
                    "first",
                    item,
                    allMilestones[pos1])
                setCardData(holder,
                    "second",
                    "first",
                    item,
                    allMilestones[pos2])
            } else if (index == (len - 2)) {
                firstDotImg.setImageResource(R.drawable.ic_ellipse_124)
                cardNotOnFirstMitraCompletedImg.visibility = View.VISIBLE

                if (len > 3) {
                    pos1 = index - 1
                    pos2 = index
                }
                setCardData(holder,
                    "first",
                    "second",
                    item,
                    allMilestones[pos1])
                setCardData(holder,
                    "second",
                    "second",
                    item,
                    allMilestones[pos2])
                if (currentTrips === milestoneTrips) {
                    onSecond.visibility = View.VISIBLE
                } else {
                    betweenFirstAndSecond.visibility = View.VISIBLE
                }
            } else if (index == (len - 1)) {
                firstDotImg.setImageResource(R.drawable.ic_ellipse_124)
                secondDotImg.setImageResource(R.drawable.ic_ellipse_124)
                cardNotOnFirstMitraCompletedImg.visibility = View.VISIBLE
                cardNotOnSecondMitraCompletedImg.visibility = View.VISIBLE

                if (len > 3) {
                    pos1 = index - 2
                    pos2 = index - 1
                    pos3 = index
                }
                setCardData(holder,
                    "first",
                    "third",
                    item,
                    allMilestones[pos1])
                setCardData(holder,
                    "second",
                    "third",
                    item,
                    allMilestones[pos2])
                setCardData(holder,
                    "third",
                    "third",
                    item,
                    allMilestones[pos3])

                var condition: Boolean
                if (isMitra)
                    condition =
                        currentTrips[0] > milestoneTrips[milestoneTrips.size - 2] && currentTrips[currentTrips.size - 1] > milestoneTrips[milestoneTrips.size - 1]
                else
                    condition = currentTrips[0] > milestoneTrips[milestoneTrips.size - 1]

                if (currentTrips === milestoneTrips || condition) {
                    lastDotImg.visibility = View.GONE
                    mainLineEndImg.visibility = View.GONE
                    onThird.visibility = View.VISIBLE
                } else {
                    betweenSecondAndThird.visibility = View.VISIBLE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCardData(
        holder: MyViewHolder,
        cardNumber: String,
        expanded: String,
        allData: EarnDataModel.IncentiveStructures.IncentiveList,
        cardData: EarnDataModel.IncentiveStructures.IncentiveList.Milestone,
    ) {
        holder.binding.apply {
            if (cardNumber == "first") {
                if (expanded == "first") {
                    cardOnFirst.visibility = View.VISIBLE
                    allData.companies?.get(0)?.companyIcon?.let { setImage(it, cardOnFirstLeftImg) }
                    cardOnFirstLeftTxt.text =
                        cardData.companyTargets?.get(0)?.target.toString() + context.resources.getString(R.string._trips)
                    cardOnFirstRightTxt.text = "₹ ${cardData.amount}"
                    cardOnFirstBottomTxt.text = allData.messageLabel
                    if (allData.companyTitle == "Mitra Weekly Incentives") {
                        cardOnFirstMitraParent.visibility = View.VISIBLE
                        cardOnFirstMitraTxt.text =
                            cardData.companyTargets?.get(1)?.target.toString() + context.resources.getString(R.string._trips)
                        allData.companies?.get(1)?.companyIcon?.let {
                            setImage(it,
                                cardOnFirstMitraImg)
                        }
                    }

                } else {
                    cardOnFirst.visibility = View.GONE
                    cardNotOnFirst.visibility = View.VISIBLE
                    //cardNotOnFirstLeftImg.setImageBitmap(https://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview)
                    cardNotOnFirstLeftTxt.text =
                        cardData.companyTargets?.get(0)?.target.toString() + context.resources.getString(R.string._trips)
                    cardNotOnFirstRightTxt.text = "₹ ${cardData.amount}"

                    allData.companies?.get(0)?.companyIcon?.let {
                        setImage(it,
                            cardNotOnFirstLeftImg)
                    }

                    if (allData.companyTitle == "Mitra Weekly Incentives") {
                        cardNotOnFirstMitraParent.visibility = View.VISIBLE
                        cardNotOnFirstMitraTxt.text =
                            cardData.companyTargets?.get(1)?.target.toString() + context.resources.getString(R.string._trips)

                        allData.companies?.get(1)?.companyIcon?.let {
                            setImage(it,
                                cardNotOnFirstMitraImg)
                        }
                    }
                }
            } else if (cardNumber == "second") {
                if (expanded == "second") {
//                    defaultTimelineState.weightSum = 0.5F
                    cardOnFirst.visibility = View.GONE
                    cardNotOnFirst.visibility = View.VISIBLE
                    cardOnSecond.visibility = View.VISIBLE
                    cardNotOnSecond.visibility = View.GONE
                    cardOnSecondLeftTxt.text =
                        cardData.companyTargets?.get(0)?.target.toString() + context.resources.getString(R.string._trips)
                    cardOnSecondRightTxt.text = "₹ ${cardData.amount}"
                    cardOnSecondBottomTxt.text = allData.messageLabel

                    allData.companies?.get(0)?.companyIcon?.let {
                        setImage(it,
                            cardOnSecondLeftImg)
                    }

                    if (allData.companyTitle == "Mitra Weekly Incentives") {
                        cardOnSecondMitraParent.visibility = View.VISIBLE
                        cardOnSecondMitraTxt.text =
                            cardData.companyTargets?.get(1)?.target.toString() + context.resources.getString(R.string._trips)

                        allData.companies?.get(1)?.companyIcon?.let {
                            setImage(it,
                                cardOnSecondMitraImg)
                        }
                    }

                } else {
                    cardOnSecond.visibility = View.GONE
                    cardNotOnSecond.visibility = View.VISIBLE
                    cardNotOnSecondLeftTxt.text =
                        cardData.companyTargets?.get(0)?.target.toString() + context.resources.getString(R.string._trips)
                    cardNotOnSecondRightTxt.text = "₹ ${cardData.amount}"

                    allData.companies?.get(0)?.companyIcon?.let {
                        setImage(it,
                            cardNotOnSecondLeftImg)
                    }

                    if (allData.companyTitle == "Mitra Weekly Incentives") {
                        cardNotOnSecondMitraParent.visibility = View.VISIBLE
                        cardNotOnSecondMitraTxt.text =
                            cardData.companyTargets?.get(1)?.target.toString() + context.resources.getString(R.string._trips)

                        allData.companies?.get(1)?.companyIcon?.let {
                            setImage(it,
                                cardNotOnSecondMitraImg)
                        }
                    }
                }
            } else if (cardNumber == "third") {
                if (expanded == "third") {
                    cardOnFirst.visibility = View.GONE
                    cardNotOnFirst.visibility = View.VISIBLE
                    cardOnSecond.visibility = View.GONE
                    cardNotOnSecond.visibility = View.VISIBLE
                    cardNotOnThirdParent.visibility = View.GONE
                    cardOnThird.visibility = View.VISIBLE
                    cardOnThirdLeftTxt.text =
                        cardData.companyTargets?.get(0)?.target.toString() + context.resources.getString(R.string._trips)
                    cardOnThirdRightTxt.text = "₹ ${cardData.amount}"
                    cardOnThirdBottomTxt.text = allData.messageLabel

                    allData.companies?.get(0)?.companyIcon?.let {
                        setImage(it,
                            cardOnThirdLeftImg)
                    }

                    if (allData.companyTitle == "Mitra Weekly Incentives") {
                        cardOnThirdMitraParent.visibility = View.VISIBLE
                        cardOnThirdMitraTxt.text =
                            cardData.companyTargets?.get(1)?.target.toString() + context.resources.getString(R.string._trips)

                        allData.companies?.get(1)?.companyIcon?.let {
                            setImage(it,
                                cardOnThirdMitraImg)
                        }
                    }
                } else {
                    cardNotOnThirdLeftTxt.text =
                        cardData.companyTargets?.get(0)?.target.toString() + context.resources.getString(R.string._trips)
                    cardNotOnThirdRightTxt.text = "₹ ${cardData.amount}"

                    allData.companies?.get(0)?.companyIcon?.let {
                        setImage(it,
                            cardNotOnThirdLeftImg)
                    }

                    if (allData.companyTitle == "Mitra Weekly Incentives") {
                        cardNotOnThirdMitraParent.visibility = View.VISIBLE
                        cardNotOnThirdMitraTxt.text =
                            cardData.companyTargets?.get(1)?.target.toString() + context.resources.getString(R.string._trips)

                        allData.companies?.get(1)?.companyIcon?.let {
                            setImage(it,
                                cardNotOnThirdMitraImg)
                        }
                    }
                }
            }
        }
    }

    private fun setWeights(
        newWeight: Float,
        width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    ): LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            newWeight
        )
    }

    private fun setImage(url: String, imageView: ImageView) {
        val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(context)
            .`as`(PictureDrawable::class.java)
            .placeholder(R.drawable.dialog_icon)
            .error(R.drawable.dialog_icon)
            .listener(SvgSoftwareLayerSetter())

        val uri = Uri.parse(url)
        requestBuilder.load(uri).into(imageView)
    }

    override fun getItemCount(): Int {
        return milestoneData.incentiveList.size ?: 0
    }
}