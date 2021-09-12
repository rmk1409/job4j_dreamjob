const validate = () => {
    let isValid = true;
    $('input').each((_, el) => {
        if (!el.value) {
            alert('All fields must be filled');
            isValid = false;
            return false;
        }
    });
    return isValid;
}