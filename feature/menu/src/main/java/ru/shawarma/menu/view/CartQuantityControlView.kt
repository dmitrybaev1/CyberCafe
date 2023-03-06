package ru.shawarma.menu.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import ru.shawarma.menu.R

class CartQuantityControlView(context: Context,attributeSet: AttributeSet): LinearLayout(context,attributeSet) {
    interface OnPlusClickListener{
        fun onPlusClick(newNumber: Int)
    }
    interface OnMinusClickListener{
        fun onMinusClick(newNumber: Int)
    }

    private var plusClickListener: OnPlusClickListener? = null
    private var minusClickListener: OnMinusClickListener? = null
    private val minusButton: Button
    private val plusButton: Button
    private val countTextView: TextView

    var count = 1
        set(value){
            field = value
            countTextView.text = field.toString()
        }


    init{
        View.inflate(context, R.layout.cart_quantity_control_view,this)
        minusButton = findViewById<Button>(R.id.minusButton)
        plusButton = findViewById<Button>(R.id.plusButton)
        countTextView = findViewById<TextView>(R.id.countTextView)
        countTextView.text = count.toString()
        minusButton.setOnClickListener {
            if(count>1)
                minusClickListener?.onMinusClick(--count)
            else
                minusClickListener?.onMinusClick(0)
        }
        plusButton.setOnClickListener {
            plusClickListener?.onPlusClick(++count)
        }
    }

    fun setOnPlusClickListener(listener: OnPlusClickListener){
        plusClickListener = listener
    }

    fun setOnPlusClickListener(block: (Int) -> Unit){
        plusClickListener = object : OnPlusClickListener {
            override fun onPlusClick(newNumber: Int) = block(newNumber)
        }
    }

    fun setOnMinusClickListener(listener: OnMinusClickListener){
        minusClickListener = listener
    }

    fun setOnMinusClickListener(block: (Int) -> Unit){
        minusClickListener = object : OnMinusClickListener {
            override fun onMinusClick(newNumber: Int) = block(newNumber)
        }
    }
}