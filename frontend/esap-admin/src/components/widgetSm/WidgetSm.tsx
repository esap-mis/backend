import './widgetSm.scss';
import React, {useEffect, useState} from "react";
import {Patient} from "../../model/Patient";
import HttpService from "../../service/HttpService";
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import Avatar from '@mui/material/Avatar';

const WidgetSm: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([]);

  useEffect(() => {
    HttpService.getLatestPatients()
      .then((response) => {
        setPatients(response);
      });
  }, []);

  return (
    <div className='widgetSm'>
      <span className='title'>Новые пациенты</span>
      <List dense={true}>
        {patients.map((patient) => (
          <ListItem key={patient.id}>
            <ListItemButton>
              <ListItemAvatar>
                <Avatar alt={`${patient.firstName} ${patient.lastName}`} />
              </ListItemAvatar>
              <ListItemText
                primary={`${patient.lastName} ${patient.firstName} ${patient.patronymic}`}
                secondary={`${patient.gender === 1 ? 'Мужской' : 'Женский'}, ${patient.birthDate}`}
              />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </div>
  );
};

export default WidgetSm;
