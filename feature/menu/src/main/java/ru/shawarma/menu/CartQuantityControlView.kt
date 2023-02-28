package ru.shawarma.menu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class CartQuantityControlView(context: Context,attributeSet: AttributeSet): LinearLayout(context,attributeSet) {
    interface OnPlusClickListener{
        fun onPlusClick(newNumber: Int)
    }
    interface OnMinusClickListener{
        fun onMinusClick(newNumber: Int)
    }

    private var plusClickListener: OnPlusClickListener? = null
    private var minusClickListener: OnMinusClickListener? = null

    var count = 1
        private set

    init{
        View.inflate(context,R.layout.cart_quantity_control_view,this)
        val minusButton = findViewById<Button>(R.id.minusButton)
        val plusButton = findViewById<Button>(R.id.plusButton)
        val countTextView = findViewById<TextView>(R.id.countTextView)
        countTextView.text = count.toString()
        minusButton.setOnClickListener {
            if(count>1)
                --count
            minusClickListener?.onMinusClick(count)
        }
        plusButton.setOnClickListener {
            ++count
            plusClickListener?.onPlusClick(count)
        }
    }

    fun setOnPlusClickListener(listener: OnPlusClickListener){
        plusClickListener = listener
    }

    fun setOnPlusClickListener(block: (Int) -> Unit){
        plusClickListener = object : OnPlusClickListener{
            override fun onPlusClick(newNumber: Int) = block(newNumber)
        }
    }

    fun setOnMinusClickListener(listener: OnMinusClickListener){
        minusClickListener = listener
    }

    fun setOnMinusClickListener(block: (Int) -> Unit){
        minusClickListener = object : OnMinusClickListener{
            override fun onMinusClick(newNumber: Int) = block(newNumber)
        }
    }
}