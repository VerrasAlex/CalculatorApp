package com.udemy.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {
	
	private var tvInput: TextView? = null
	
	//Variables that help us know if the last entry in the TextView was a Numeric or a Dot respectively
	//See "onDecimal" function
	var lastNumeric: Boolean = false
	var lastDot: Boolean = false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		tvInput = findViewById(R.id.tvInput)
	}
	
	//See the buttons in activity_main.xml
	//Although, it is NOT recommended each Button runs one of the
	//methods below when clicked using android:onClick="nameOfMethod"
	
	//Implementation of Digit buttons
	fun onDigit(view: View) {
		//Toast.makeText(this, "Button Clicked!",Toast.LENGTH_SHORT).show()
		
		//Variable "view" is the ACTUAL button that was pressed and called the "onDigit" function
		//Therefore, we can use this view AS a button
		tvInput?.append((view as Button).text)
		
		//Set the "lastNumeric" to true to point out that the last entry was a numeric value and NOT a Dot
		lastNumeric = true
		
		//Set the "lastDot" to false to point out that the last entry was a numeric value and NOT a Dot
		lastDot = false
	}
	
	//Implementation of Operator buttons
	fun onOperator(view: View) {
		//if "tvInput" is Not null and its "text" is NOT empty then...
		tvInput?.text?.let {
			//if (the last entry was numeric) AND (is an operator NOT added already) to the "tvInput"
			// "it" resembles the variable sequence used before "let" (tvInput.text)
			if (lastNumeric && !isOperatorAdded(it.toString())) {
				tvInput?.append((view as Button).text)
				lastNumeric = false
				lastDot = false
			}
		}
	}
	
	//Function that returns a Boolean if an operator was already added in "tvInput"
	private fun isOperatorAdded(value: String): Boolean {
		
		var result = value
		//Don't count the "-" at the beginning as an operator (ignore it)
		//This way we CAN do operations with the first value being negative.
		//Notice: We CANNOT do operations with negatives completely.
		if (result.startsWith("-")) {
			result = value.substring(1)
		}
		
		//We check if "tvInput"/"value" contains an operator already (except the negative prefix if it exists)
		return result.contains("+") ||
				result.contains("-") ||
				result.contains("*") ||
				result.contains("/")
		
			// Notice: ".contains()" function itself returns a boolean value,that is why
			// we don't check contains in the else conditions and then return "true" like
			// 		}else if (value.contains("+") || ...){
			//			true
			// 		}
	}
	
	//Implementation of EQUAL button
	fun onEqual(view: View) {
		if (lastNumeric) {
			var tvValue = tvInput?.text.toString()
			var prefix = ""
			
			try {
				// if "tvValue" starts with "-" then ignore it and use as "tvValue" that starts
				// at index 1 till the end of it
				if (tvValue.startsWith("-")) {
					prefix = "-"
					tvValue = tvValue.substring(1)
				}
				
				//Subtraction
				if (tvValue.contains("-")) {
					val splitValue = tvValue.split("-")
					
					var one = splitValue[0]
					var two = splitValue[1]
					
					//if there was a prefix (the first value was negative) the add it again to it,
					// so that the operation will work as expected
					// If we do not do this the operation i.e. "-2-3" will give "-1" because the
					// first "-" is ignored so what the operation really would look like is "2-3"
					
					if (prefix.isNotEmpty()) {
						//We can just append strings together using "+"
						one = prefix + one
					}
					
					tvInput?.text = removeZeroAfterDot((one.toDouble() - two.toDouble()).toString())
				}
				
				//Addition
				else if (tvValue.contains("+")) {
					val splitValue = tvValue.split("+")
					
					var one = splitValue[0]
					var two = splitValue[1]
					
					if (prefix.isNotEmpty()) {
						//We can just append strings together using "+"
						one = prefix + one
					}
					
					tvInput?.text = removeZeroAfterDot((one.toDouble() + two.toDouble()).toString())
				}
				
				//Multiplication
				else if (tvValue.contains("*")) {
					val splitValue = tvValue.split("*")
					
					var one = splitValue[0]
					var two = splitValue[1]
					
					if (prefix.isNotEmpty()) {
						//We can just append strings together using "+"
						one = prefix + one
					}
					
					tvInput?.text = removeZeroAfterDot((one.toDouble() * two.toDouble()).toString())
				}
				
				//Division
				else if (tvValue.contains("/")) {
					val splitValue = tvValue.split("/")
					
					var one = splitValue[0]
					var two = splitValue[1]
					
					if (prefix.isNotEmpty()) {
						//We can just append strings together using "+"
						one = prefix + one
					}
					
					tvInput?.text = removeZeroAfterDot((one.toDouble() / two.toDouble()).toString())
				}
			}
			
			// ArithmeticException: catch errors such as dividing by 0 or calculation
			// that do not work on an arithmetic level
			catch (e: ArithmeticException) {
				//print the error onto the "Logcat" section below
				e.printStackTrace()
			}
		}
	}
	
	//Method that removes the ".0" (if it exists) from the result that it's given to "tvInput"
	private fun removeZeroAfterDot(result: String): String {
		var value = result
		
		//if the result contains ".0" then set the "value" to be the substring of result
		// from the beginning till before the last 2 indexes (.0)
		if (result.contains(".0")) {
			value = result.substring(0, result.length - 2)
		}
		return value
	}
	
	//Implementation of CLEAR button
	fun onCLR(view: View) {
		//We just put an empty String in the TextView whenever "CLR" is clicked
		tvInput?.text = ""
	}
	
	fun onDecimal(view: View) {
		// if the last entry was a Numeric and NOT a Dot then...
		if (lastNumeric && !lastDot && !containsDot()) {
			tvInput?.append(".")
			lastNumeric = false
			lastDot = true
		}
	}
	
	//Function that checks for dots in the "tvInput" every time (even after the result of an operation)
	//This way we can NOT enter more than one dot per value e.g. 3.2.4578.2 + 23.31.2
	private fun containsDot(): Boolean {
		
		var value = tvInput?.text.toString()
		var splitValue: List<String> ?= null
		var two = ""
		
		try {
			//We ignore the negative prefix if it exists in "tvInput" / "value"
			if (value.startsWith("-")) {
				value = value.substring(1)
			}
			
			//We split the "tvInput" / "value" o the operators
			if (value.contains("+")) {
				splitValue = value.split("+")
			} else if (value.contains("-")) {
				splitValue = value.split("-")
			} else if (value.contains("*")) {
				splitValue = value.split("*")
			} else if (value.contains("/")) {
				splitValue = value.split("/")
			}
			
			//"two" is the part of "tvInput" / "value" that follows the operator
			two = splitValue?.get(1).toString()
			
		} catch (e:NullPointerException){
			e.printStackTrace()
		}
		
		// If the splitted value IS null (an operator exists in "tvInput" / "value") then
		// check if the "tvInput" / "value" has a DOT
		return if (splitValue==null) {
			value.contains(".")
		} else{
		// else check if the part after the operator has a DOT
			two.contains(".")
		}
	}
}

