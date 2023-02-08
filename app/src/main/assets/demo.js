function submit() {
    var values = {};
    Array.prototype.slice.call(document.getElementsByTagName('input')).forEach(function(el) {
        if (el.type !== 'button' && el.type !== 'radio') {
            if (el.id === 'zip_code') {
                values[el.id] = parseInt(el.value);
            } else {
                values[el.id] = el.value;
            }
        }
    });
    if (isChecked('input_json')) {
        Android.onSubmit('teste');
    } else if (isChecked('input_literal')) {
        Android.onSubmitWithFirstnameAndLastnameAndAddress1AndAddress2AndZipcodeAndPhonenumber(values['mail'], values['first_name'], values['last_name'], values['address_line_1'], values['address_line_2'], parseInt(values['zip_code']), `${values['phone_number']}`);
    }
}

function clearAll() {
    Array.prototype.slice.call(document.getElementsByTagName('input')).forEach(function(el) {
        if (el.type !== 'button' && el.type !== 'radio') {
            el.value = '';
        }
    });
}

function isChecked(id) {
    return document.getElementById(id).checked;
};

async function checkSumbit() {
    const result = await Android.isSubmitted;
    alert(result ? 'Submitted.' : 'Not submitted.');
}
