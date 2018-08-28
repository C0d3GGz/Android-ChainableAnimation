package de.thkoeln.chainableanimation

import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator

sealed class ChainableAnimation<T: ViewGroup> {

    protected var finalAction: (T) -> Unit = {}
    protected abstract val view: T

    abstract fun execute()

    fun finally(finalAction: (T) -> Unit): ChainableAnimation<T> {
        this.finalAction = finalAction
        return this
    }

    class Single<T: ViewGroup>(override val view: T, private val transition: Transition? = null,
                               private val animationAction: (T) -> Unit): ChainableAnimation<T>() {

        private val fallback: Transition by lazy {
            ChangeBounds().apply {
                interpolator = AccelerateInterpolator()
            }
        }

        override fun execute() {
            val mTransition = transition ?: fallback
            mTransition.addListener(transitionEndListener { finalAction(view) })
            TransitionManager.beginDelayedTransition(view, mTransition)

            animationAction(view)
        }
    }

    class Sequence<T: ViewGroup>(override val view: T): ChainableAnimation<T>() {
        private val animations: MutableList<Single<T>> = mutableListOf()

        fun push(animation: (T) -> Unit, transition: Transition? = null): Sequence<T> {
            animations += Single(view, transition, animation)
            return this
        }

        override fun execute() {
            if (animations.isNotEmpty()) {
                val first = animations.removeAt(0)
                first.finally { this.execute() }.execute()
            } else {
                finalAction(view)
            }
        }
    }
}

private fun transitionEndListener(onTransitionEndedAction: () -> Unit) =
        object : Transition.TransitionListener{

            override fun onTransitionEnd(transition: Transition?) {
                onTransitionEndedAction()
            }

            override fun onTransitionResume(transition: Transition?) {}
            override fun onTransitionPause(transition: Transition?) {}
            override fun onTransitionCancel(transition: Transition?) {}
            override fun onTransitionStart(transition: Transition?) {}
        }