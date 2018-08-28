package de.thkoeln.android

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ImageView
import de.thkoeln.chainableanimation.ChainableAnimation
import de.thkoeln.chainableanimation.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var imageId = addImage(true, main_activity_layout)
        plusButton.setOnClickListener{
            animateViewFromBottomToTop(main_activity_layout, imageId)
            imageId = addImage(true, main_activity_layout)
        }

        var otherImageId = addImage(false, main_activity_layout)
        minusButton.setOnClickListener{
            animateViewFromTopToBottom(main_activity_layout, otherImageId)
            otherImageId = addImage(false, main_activity_layout)
        }

    }

    private fun animateViewFromTopToBottom(rootLayout: ConstraintLayout, otherImageId: Int) {
        ChainableAnimation.Sequence(rootLayout)
                .push( { upperToMiddleTransition(it, otherImageId) })
                .push( { middleToLowerTransition(it, otherImageId)})
                .finally { it.removeView(findViewById(otherImageId)) }
                .execute()
    }

    private fun animateViewFromBottomToTop(rootLayout: ConstraintLayout, idOfViewToAnimate: Int){
        ChainableAnimation.Sequence(rootLayout)
                .push( { lowerToMiddleTransition(it, idOfViewToAnimate) })
                .push( { middleToUpperTransition(it, idOfViewToAnimate)})
                .finally { it.removeView(findViewById(idOfViewToAnimate)) }
                .execute()

    }

    private fun upperToMiddleTransition(layout: ConstraintLayout, imageId: Int) {
        layout.applyConstraintSet {
            connect(
                    TOP of imageId to BOTTOM of R.id.upperBackground,
                    BOTTOM of imageId to TOP of R.id.lowerBackground
            )
        }
    }

    private fun middleToLowerTransition(layout: ConstraintLayout, imageId: Int) {
        layout.applyConstraintSet {
            connect(
                    TOP of imageId to TOP of R.id.lowerBackground,
                    BOTTOM of imageId to BOTTOM of R.id.lowerBackground
            )
        }
    }

    private fun lowerToMiddleTransition(layout: ConstraintLayout, imageId: Int){
        layout.applyConstraintSet {
            connect(
                    TOP of imageId to BOTTOM of R.id.lowerBackground,
                    BOTTOM of imageId to TOP of R.id.upperBackground)
        }
    }

    private fun middleToUpperTransition(layout: ConstraintLayout, imageId: Int){
        layout.applyConstraintSet {
            connect(
                    TOP of imageId to TOP of R.id.upperBackground,
                    BOTTOM of imageId to BOTTOM of R.id.upperBackground)
        }
    }

    private fun addImage(lower: Boolean, layout: ConstraintLayout): Int {
        val imgView = ImageView(layout.context)
        imgView.backgroundResource = android.R.drawable.btn_star_big_on
        imgView.id = View.generateViewId()
        layout.addView(imgView)

        val bindTo = if(lower) R.id.lowerBackground else R.id.upperBackground

        layout.applyConstraintSet {
            imgView{
                connect(
                        START to START of bindTo ,
                        END to END of bindTo ,
                        TOP to TOP of bindTo ,
                        BOTTOM to BOTTOM of bindTo
                )

                setTranslationZ(imgView.id, -100f)
            }

        }

        return imgView.id
    }
}
