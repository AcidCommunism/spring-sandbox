import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer, updateCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>{meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>{meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const EditCustomerForm = ({id, name, age, email, gender, fetchCustomers}) => {
    const minYearsThreshold = 16;
    const maxYearsThreshold = 100;
    return (
        <>
            <Formik
                initialValues={{
                    name: name,
                    email: email,
                    age: age,
                    gender: gender,
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(minYearsThreshold, `Must be at least ${minYearsThreshold} yo`)
                        .max(maxYearsThreshold, `Must be less than ${maxYearsThreshold} yo`)
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(['MALE', 'FEMALE', 'NONE_OF_YOUR_BUSINESS'])
                        .required()
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    customer.id = id;
                    setSubmitting(true);
                    updateCustomer(customer)
                        .then(response => {
                            successNotification(
                                'Customer updated',
                                `${customer.name} was successfully updated!`
                            );
                        })
                        .catch(err =>
                            errorNotification(
                                `Server returned an error code ${err.response.status}`,
                                `Error: ${err.response.data.message}`
                            )
                        )
                        .finally(() => {
                            setSubmitting(false);
                            fetchCustomers();
                        });
                }}
            >
                {(formik) => {
                    return (
                        <Form>
                            <Stack spacing={"24px"}>

                                <MyTextInput
                                    label="Name"
                                    name="name"
                                    type="text"
                                />

                                <MyTextInput
                                    label="Email Address"
                                    name="email"
                                    type="email"
                                />

                                <MyTextInput
                                    label="Age"
                                    name="age"
                                    type="number"
                                />

                                <MySelect label="Gender" name="gender">
                                    <option value="">Select customer gender</option>
                                    <option value="MALE">MALE</option>
                                    <option value="FEMALE">FEMALE</option>
                                    <option value="NONE_OF_YOUR_BUSINESS">None of your business</option>
                                </MySelect>

                                <Button mt={10}
                                        type="submit"
                                        isDisabled={!formik.isValid || formik.isSubmitting}
                                >
                                    Submit
                                </Button>
                            </Stack>
                        </Form>
                    )
                }}
            </Formik>
        </>
    );
};

export default EditCustomerForm;